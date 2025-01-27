package com.example.demo.services.impl;

import com.example.demo.dto.*;
import com.example.demo.models.*;
import com.example.demo.repository.*;
import com.example.demo.services.SalesOrderService;
import exception.InsufficientStockException;
import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ManufacturingProcessRepository processRepository;

    @Transactional
    public SalesOrderResponse createSalesOrder(SalesOrderRequest request) {
        SalesOrder salesOrder = new SalesOrder();

        // Set basic fields
        salesOrder.setOrderDate(request.orderDate());
        salesOrder.setStatus(request.status());

        // Link customer
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        salesOrder.setCustomer(customer);

        // Link manufacturing process if present
        if (request.manufacturingProcessId() != null) {
            ManufacturingProcess process = processRepository.findById(request.manufacturingProcessId())
                    .orElseThrow(() -> new ResourceNotFoundException("Process not found"));
            salesOrder.setManufacturingProcess(process);
        }

        // Create order items
        List<SalesOrderItem> items = request.items().stream()
                .map(itemRequest -> createOrderItem(itemRequest, salesOrder))
                .toList();
        salesOrder.setItems(items);

        // Generate order number
        salesOrder.setOrderNumber(generateOrderNumber());
        if(salesOrder.getStatus().equals("CONFIRMED")){
            deductStock(salesOrder);
        }
        return convertToResponse(salesOrderRepository.save(salesOrder));
    }

    @Override
    public List<SalesOrderResponse> getAllSalesOrders() {
        // Retrieve all sales orders from the database and convert to DTOs
        return salesOrderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SalesOrderResponse getSalesOrderById(Long id) {
        // Find sales order by ID or throw not found exception
        return salesOrderRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found with id: " + id));
    }

    @Override
    @Transactional
    public SalesOrderResponse updateSalesOrder(Long id, SalesOrderRequest request) {
        // Find existing order
        SalesOrder existingOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        // Update customer if changed
        if(request.customerId() != null &&
                !existingOrder.getCustomer().getId().equals(request.customerId())) {
            Customer customer = customerRepository.findById(request.customerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            existingOrder.setCustomer(customer);
        }

        // Update order date
        if(request.orderDate() != null) {
            existingOrder.setOrderDate(request.orderDate());
        }

        // Update status
        if(request.status() != null) {
            existingOrder.setStatus(request.status());
        }

        // Update items
        if(request.items() != null) {
            updateOrderItems(existingOrder, request.items());
        }

        // Save and return updated order
        return convertToResponse(salesOrderRepository.save(existingOrder));
    }

    @Override
    @Transactional
    public void deleteSalesOrder(Long id) {
        // Check existence first
        if(!salesOrderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sales order not found");
        }
        salesOrderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public SalesOrderResponse confirmSalesOrder(Long id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        // Validate can be confirmed
        if(!"DRAFT".equals(order.getStatus())) {
            throw new IllegalStateException("Only DRAFT orders can be confirmed");
        }

       deductStock(order);

        // Update status
        order.setStatus("CONFIRMED");
        return convertToResponse(salesOrderRepository.save(order));
    }

   private void deductStock (SalesOrder order){
       // Validate stock for all items
       order.getItems().forEach(item -> {
           if(item.getProduct().getStockLevel() < item.getQuantity()) {
               throw new InsufficientStockException(
                       "Insufficient stock for product: " + item.getProduct().getName()
               );
           }
       });

       // Deduct stock
       order.getItems().forEach(item -> {
           Product product = item.getProduct();
           product.setStockLevel(product.getStockLevel() - item.getQuantity());
           productRepository.save(product);
       });
   }

    @Override
    @Transactional
    public SalesOrderResponse cancelSalesOrder(Long id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        // Only confirmed orders can be cancelled
        if(!"CONFIRMED".equals(order.getStatus())) {
            throw new IllegalStateException("Only CONFIRMED orders can be cancelled");
        }

        // Restore stock
        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStockLevel(product.getStockLevel() + item.getQuantity());
            productRepository.save(product);
        });

        // Update status
        order.setStatus("CANCELLED");
        return convertToResponse(salesOrderRepository.save(order));
    }

    // Helper method to update order items
    private void updateOrderItems(SalesOrder order, List<SalesOrderItemRequest> newItems) {
        // Clear existing items
        order.getItems().clear();

        // Add new items
        List<SalesOrderItem> items = newItems.stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.productId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                    SalesOrderItem item = new SalesOrderItem();
                    item.setProduct(product);
                    item.setQuantity(itemRequest.quantity());
                    item.setUnitPrice(itemRequest.unitPrice());
                    item.setSalesOrder(order);
                    return item;
                })
                .collect(Collectors.toList());

        order.setItems(items);
    }

    private SalesOrderItem createOrderItem(SalesOrderItemRequest request, SalesOrder salesOrder) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        validateStock(product, request.quantity());

        SalesOrderItem item = new SalesOrderItem();
        item.setProduct(product);
        item.setQuantity(request.quantity());
        item.setUnitPrice(request.unitPrice());
        item.setSalesOrder(salesOrder);
        return item;
    }

    private void validateStock(Product product, int quantity) {
        if (product.getStockLevel() < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock for " + product.getName() +
                            " (Available: " + product.getStockLevel() + ")"
            );
        }
    }

    private String generateOrderNumber() {
        return "SO-" + System.currentTimeMillis();
    }



    private SalesOrderResponse convertToResponse(SalesOrder salesOrder) {
        return new SalesOrderResponse(
                salesOrder.getId(),
                salesOrder.getOrderNumber(),
                salesOrder.getOrderDate(),
                salesOrder.getStatus(),
                convertCustomer(salesOrder.getCustomer()),
                convertItems(salesOrder.getItems()),
                convertProcess(salesOrder.getManufacturingProcess()),
                salesOrder.getCreatedDate()
        );
    }

    private CustomerResponse convertCustomer(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getGstNumber(),
                customer.getVendorCode()
        );
    }

    private List<SalesOrderItemResponse> convertItems(List<SalesOrderItem> items) {
        return items.stream()
                .map(this::convertItem)
                .toList();
    }

    private SalesOrderItemResponse convertItem(SalesOrderItem item) {
        return new SalesOrderItemResponse(
                item.getId(),
                convertProduct(item.getProduct()),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }

    private ProductResponse convertProduct(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getPrice(),
                product.getStockLevel(),
                product.getMinStockLevel(),
                product.getReservedStock(),
                product.getHsnCode(),
                product.getItemType(),
                product.getUnitOfMeasurement()
        );
    }

    private ManufacturingProcessResponse convertProcess(ManufacturingProcess process) {
        if (process == null) return null;
        return new ManufacturingProcessResponse(
                process.getId(),
                process.getManufacturingId(),
                process.getQuantityToProduce(),
                process.getExpectedCompletionDate(),
                process.getStatus(),
               convertBOM(process.getBom()),
               convertStage(process.getStages())
        );
    }

    private List<ManufacturingStageResponse> convertStage(List<ManufacturingStage> stages) {
        return stages.stream()
                .map(this::convertStage)
                .toList();
    }

    private ManufacturingStageResponse convertStage(ManufacturingStage stage) {
        return new ManufacturingStageResponse(
                stage.getId(),
                stage.getName(),
                stage.getDueDate(),
                stage.getStatus(),
                stage.getDescription()
        );
    }
    private BOMResponse convertBOM(BillOfMaterials bom) {
        return new BOMResponse(
                bom.getId(),
                bom.getName(),
                bom.getVersion(),
                bom.getCreatedDate(),
                convertProduct(bom.getProduct()),
                convertBOMItems(bom.getItems())
        );
    }

    private List<BOMItemResponse> convertBOMItems(List<BOMItem> items) {
        return items.stream()
                .map(this::convertBOMItem)
                .toList();
    }

    private BOMItemResponse convertBOMItem(BOMItem item) {
        return new BOMItemResponse(
                item.getId(),
                item.getRawMaterial().getName(),
                item.getQuantity()
        );
    }
}