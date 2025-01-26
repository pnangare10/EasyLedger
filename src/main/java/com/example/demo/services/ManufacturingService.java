package com.example.demo.services;

import com.example.demo.dto.ManufacturingProcessRequest;
import com.example.demo.dto.ManufacturingProcessResponse;
import com.example.demo.dto.ManufacturingStageResponse;
import com.example.demo.dto.ManufacturingStageUpdateRequest;
import java.util.List;

public interface ManufacturingService {
    
    // Process Management
    ManufacturingProcessResponse startProcess(ManufacturingProcessRequest request);
    List<ManufacturingProcessResponse> getAllProcesses();
    ManufacturingProcessResponse getProcessById(Long id);
    ManufacturingProcessResponse updateProcess(Long id, ManufacturingProcessRequest request);
    void deleteProcess(Long id);
    
    // Stage Management
    ManufacturingStageResponse updateStageStatus(Long processId, Long stageId, ManufacturingStageUpdateRequest request);
    List<ManufacturingStageResponse> getProcessStages(Long processId);
    
    // Production Tracking
    ManufacturingProcessResponse completeProcess(Long id);
    ManufacturingProcessResponse cancelProcess(Long id);
}