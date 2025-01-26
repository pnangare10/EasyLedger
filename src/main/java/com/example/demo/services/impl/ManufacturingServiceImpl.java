package com.example.demo.services.impl;

import com.example.demo.dto.*;
import com.example.demo.models.*;
import com.example.demo.repository.*;
import com.example.demo.services.ManufacturingService;
import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManufacturingServiceImpl implements ManufacturingService {

    private final ManufacturingProcessRepository processRepository;
    private final BOMRepository bomRepository;
    private final ProductRepository productRepository;
    private final ManufacturingStageRepository stageRepository;

    // Process Management
    @Override
    @Transactional
    public ManufacturingProcessResponse startProcess(ManufacturingProcessRequest request) {
        BillOfMaterials bom = bomRepository.findById(request.bomId())
                .orElseThrow(() -> new ResourceNotFoundException("BOM not found"));

        validateMaterialAvailability(bom, request.quantityToProduce());

        ManufacturingProcess process = createProcessEntity(request, bom);
        if (request.reserveMaterials()) {
            reserveMaterials(bom, request.quantityToProduce());
            process.setMaterialsReserved(true);
        } else {
            deductRawMaterials(bom, request.quantityToProduce());
            process.setMaterialsReserved(false);
        }

        return convertToResponse(processRepository.save(process));
    }

    @Override
    public List<ManufacturingProcessResponse> getAllProcesses() {
        return processRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public ManufacturingProcessResponse getProcessById(Long id) {
        return processRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Process not found"));
    }

    @Override
    @Transactional
    public ManufacturingProcessResponse updateProcess(Long id, ManufacturingProcessRequest request) {
        ManufacturingProcess process = processRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Process not found"));

        process.setManufacturingId(request.manufacturingId());
        process.setQuantityToProduce(request.quantityToProduce());
        process.setExpectedCompletionDate(request.expectedCompletionDate());

        return convertToResponse(processRepository.save(process));
    }

    @Override
    @Transactional
    public void deleteProcess(Long id) {
        ManufacturingProcess process = processRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Process not found"));

        if (!"CANCELLED".equals(process.getStatus())) {
            restoreMaterials(process);
        }
        processRepository.delete(process);
    }

    // Stage Management
    @Override
    @Transactional
    public ManufacturingStageResponse updateStageStatus(Long processId, Long stageId,
                                                        ManufacturingStageUpdateRequest request) {
        ManufacturingStage stage = stageRepository.findById(stageId)
                .filter(s -> s.getProcess().getId().equals(processId))
                .orElseThrow(() -> new ResourceNotFoundException("Stage not found"));

        validateStageTransition(stage.getStatus(), request.status());

        stage.setStatus(request.status());
        stage.setDueDate(request.dueDate());
        stage.setDescription(request.description());

        updateProcessStatus(stage.getProcess());
        if(stage.getProcess().getStatus() == "COMPLETED") {
            handleCompletedMaterials(stage.getProcess());
        }
        return convertStageToResponse(stageRepository.save(stage));
    }

    @Override
    public List<ManufacturingStageResponse> getProcessStages(Long processId) {
        return stageRepository.findByProcessId(processId).stream()
                .map(this::convertStageToResponse)
                .toList();
    }

    // Production Control
    @Override
    @Transactional
    public ManufacturingProcessResponse completeProcess(Long id) {
        ManufacturingProcess process = processRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Process not found"));

        validateAllStagesCompleted(process);

        handleCompletedMaterials(process);

        process.setStatus("COMPLETED");

        return convertToResponse(processRepository.save(process));
    }

    @Override
    @Transactional
    public ManufacturingProcessResponse cancelProcess(Long id) {
        ManufacturingProcess process = processRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Process not found"));

        restoreMaterials(process);
        process.setStatus("CANCELLED");

        return convertToResponse(processRepository.save(process));
    }

    // Validation Logic
    private void validateMaterialAvailability(BillOfMaterials bom, int quantity) {
        bom.getItems().forEach(item -> {
            int required = item.getQuantity() * quantity;
            if (item.getRawMaterial().getStockLevel() < required) {
                throw new IllegalStateException(
                        "Insufficient stock for " + item.getRawMaterial().getName() +
                                " (Required: " + required + ", Available: " +
                                item.getRawMaterial().getStockLevel() + ")"
                );
            }
        });
    }

    private void validateStageTransition(String currentStatus, String newStatus) {
        if ("COMPLETED".equals(currentStatus) && !"COMPLETED".equals(newStatus)) {
            throw new IllegalStateException("Completed stages cannot be modified");
        }
    }

    private void validateAllStagesCompleted(ManufacturingProcess process) {
        boolean allStagesCompleted = process.getStages().stream()
                .allMatch(stage -> "COMPLETED".equals(stage.getStatus()));

        if (!allStagesCompleted) {
            throw new IllegalStateException("All stages must be completed first");
        }
    }

    private void reserveMaterials(BillOfMaterials bom, int quantity) {
        bom.getItems().forEach(item -> {
            Product material = item.getRawMaterial();
            int required = item.getQuantity() * quantity;

            if ((material.getStockLevel() - material.getReservedStock()) < required) {
                throw new IllegalStateException("Insufficient available stock for: " + material.getName());
            }

            material.reserveStock(required);
            productRepository.save(material);
        });
    }

    // Inventory Operations
    private void deductRawMaterials(BillOfMaterials bom, int quantity) {
        bom.getItems().forEach(item -> {
            Product material = item.getRawMaterial();
            material.setStockLevel(material.getStockLevel() - (item.getQuantity() * quantity));
            material.setReservedStock(material.getReservedStock() - (item.getQuantity() * quantity));
            productRepository.save(material);
        });
    }

    private void handleMaterialDeduction(BillOfMaterials bom, int quantity, boolean reserveMaterials) {
        if (reserveMaterials) {
            reserveMaterials(bom, quantity);
        } else {
            deductMaterialsImmediately(bom, quantity);
        }
    }

    private void deductMaterialsImmediately(BillOfMaterials bom, int quantity) {
        bom.getItems().forEach(item -> {
            Product material = item.getRawMaterial();
            int required = item.getQuantity() * quantity;

            if (material.getStockLevel() < required) {
                throw new IllegalStateException("Insufficient stock for: " + material.getName());
            }

            material.setStockLevel(material.getStockLevel() - required);
            productRepository.save(material);
        });
    }

    private void releaseReservedMaterials(ManufacturingProcess process) {
        process.getBom().getItems().forEach(item -> {
            Product material = item.getRawMaterial();
            int reserved = item.getQuantity() * process.getQuantityToProduce();
            material.releaseReservedStock(reserved);
            productRepository.save(material);
        });
    }


    private void handleCompletedMaterials(ManufacturingProcess process) {
        if (process.getMaterialsReserved()) {
            // Convert reserved stock to actual deduction
            process.getBom().getItems().forEach(item -> {
                Product material = item.getRawMaterial();
                int reservedQty = item.getQuantity() * process.getQuantityToProduce();

                material.releaseReservedStock(reservedQty);
                material.setStockLevel(material.getStockLevel() - reservedQty);
                productRepository.save(material);
            });
        }
        // Add finished goods regardless of reservation status
        addFinishedGoodsToInventory(process);
    }

    private void restoreMaterials(ManufacturingProcess process) {
        if (process.getMaterialsReserved()) {
            // Release reserved materials without deducting
            process.getBom().getItems().forEach(item -> {
                Product material = item.getRawMaterial();
                int reservedQty = item.getQuantity() * process.getQuantityToProduce();
                material.releaseReservedStock(reservedQty);
                productRepository.save(material);
            });
        } else {
            // Restore immediately deducted materials
            process.getBom().getItems().forEach(item -> {
                Product material = item.getRawMaterial();
                int qty = item.getQuantity() * process.getQuantityToProduce();
                material.setStockLevel(material.getStockLevel() + qty);
                productRepository.save(material);
            });
        }
    }

    private void addFinishedGoodsToInventory(ManufacturingProcess process) {
        Product finishedProduct = process.getBom().getProduct();
        finishedProduct.setStockLevel(finishedProduct.getStockLevel() +
                process.getQuantityToProduce());
        productRepository.save(finishedProduct);
    }

    // Entity Conversions
    private ManufacturingProcess createProcessEntity(ManufacturingProcessRequest request,
                                                     BillOfMaterials bom) {
        ManufacturingProcess process = new ManufacturingProcess();
        process.setManufacturingId(request.manufacturingId());
        process.setBom(bom);
        process.setQuantityToProduce(request.quantityToProduce());
        process.setExpectedCompletionDate(request.expectedCompletionDate());
        process.setStatus("STARTED");

        List<ManufacturingStage> stages = request.stages().stream()
                .map(stageRequest -> {
                    ManufacturingStage stage = new ManufacturingStage();
                    stage.setName(stageRequest.name());
                    stage.setDueDate(stageRequest.dueDate());
                    stage.setStatus("PENDING");
                    stage.setDescription(stageRequest.description());
                    stage.setProcess(process);
                    return stage;
                }).toList();

        process.setStages(stages);
        return process;
    }

    private ManufacturingProcessResponse convertToResponse(ManufacturingProcess process) {
        return new ManufacturingProcessResponse(
                process.getId(),
                process.getManufacturingId(),
                process.getQuantityToProduce(),
                process.getExpectedCompletionDate(),
                process.getStatus(),
                convertToBOMResponse(process.getBom()),
                process.getStages().stream()
                        .map(this::convertStageToResponse)
                        .toList()
        );
    }

    private ManufacturingStageResponse convertStageToResponse(ManufacturingStage stage) {
        return new ManufacturingStageResponse(
                stage.getId(),
                stage.getName(),
                stage.getDueDate(),
                stage.getStatus(),
                stage.getDescription()
        );
    }

    private BOMResponse convertToBOMResponse(BillOfMaterials bom) {
        return new BOMServiceImpl(bomRepository, productRepository).convertToResponse(bom);
    }

    private void updateProcessStatus(ManufacturingProcess process) {
        // Determine process status based on stages
        long completedStages = process.getStages().stream()
                .filter(stage -> "COMPLETED".equals(stage.getStatus()))
                .count();

        long totalStages = process.getStages().size();

        if (completedStages == totalStages) {
            process.setStatus("COMPLETED");
        } else if (completedStages > 0) {
            process.setStatus("IN_PROGRESS");
        } else {
            process.setStatus("STARTED");
        }

        // Update completion date if last stage completed
        if (process.getStatus().equals("COMPLETED")) {
            process.setExpectedCompletionDate(LocalDate.now());
        }

        processRepository.save(process);
    }
}