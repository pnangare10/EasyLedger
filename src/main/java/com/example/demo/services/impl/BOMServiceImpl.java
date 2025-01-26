package com.example.demo.services.impl;

import com.example.demo.dto.BOMRequest;
import com.example.demo.dto.BOMResponse;
import com.example.demo.dto.BOMItemResponse;
import com.example.demo.dto.ProductResponse;
import com.example.demo.models.BillOfMaterials;
import com.example.demo.models.BOMItem;
import com.example.demo.models.Product;
import com.example.demo.repository.BOMRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.services.BOMService;
import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BOMServiceImpl implements BOMService {

    private final BOMRepository bomRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public BOMResponse createBOM(BOMRequest bomRequest) {
        Product product = productRepository.findById(bomRequest.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        BillOfMaterials bom = new BillOfMaterials();
        bom.setName(bomRequest.name());
        bom.setVersion(bomRequest.version());
        bom.setCreatedDate(LocalDateTime.now());
        bom.setProduct(product);

        List<BOMItem> items = bomRequest.items().stream()
                .map(itemRequest -> {
                    Product rawMaterial = productRepository.findById(itemRequest.rawMaterialId())
                            .orElseThrow(() -> new ResourceNotFoundException("Raw material not found"));
                    BOMItem item = new BOMItem();
                    item.setBom(bom);
                    item.setRawMaterial(rawMaterial);
                    item.setQuantity(itemRequest.quantity());
                    return item;
                }).toList();

        bom.setItems(items);
        return convertToResponse(bomRepository.save(bom));
    }

    @Override
    public List<BOMResponse> getAllBOMs() {
        return bomRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public BOMResponse getBOMById(Long id) {
        return bomRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("BOM not found"));
    }

    @Override
    @Transactional
    public BOMResponse updateBOM(Long id, BOMRequest bomRequest) {
        BillOfMaterials existingBOM = bomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BOM not found"));

        // Update basic fields
        existingBOM.setName(bomRequest.name());
        existingBOM.setVersion(bomRequest.version());

        // Clear existing items and add new ones
        existingBOM.getItems().clear();
        List<BOMItem> newItems = bomRequest.items().stream()
                .map(itemRequest -> {
                    Product rawMaterial = productRepository.findById(itemRequest.rawMaterialId())
                            .orElseThrow(() -> new ResourceNotFoundException("Raw material not found"));
                    BOMItem item = new BOMItem();
                    item.setBom(existingBOM);
                    item.setRawMaterial(rawMaterial);
                    item.setQuantity(itemRequest.quantity());
                    return item;
                }).toList();

        existingBOM.setItems(newItems);
        return convertToResponse(bomRepository.save(existingBOM));
    }

    @Override
    public void deleteBOM(Long id) {
        if (!bomRepository.existsById(id)) {
            throw new ResourceNotFoundException("BOM not found");
        }
        bomRepository.deleteById(id);
    }

    BOMResponse convertToResponse(BillOfMaterials bom) {
        List<BOMItemResponse> itemResponses = bom.getItems().stream()
                .map(item -> new BOMItemResponse(
                        item.getRawMaterial().getId(),
                        item.getRawMaterial().getName(),
                        item.getQuantity()
                )).toList();

        return new BOMResponse(
                bom.getId(),
                bom.getName(),
                bom.getVersion(),
                bom.getCreatedDate(),
                convertToProductResponse(bom.getProduct()),
                itemResponses
        );
    }

    private ProductResponse convertToProductResponse(Product product) {
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
}