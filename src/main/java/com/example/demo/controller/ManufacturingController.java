package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.services.ManufacturingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturing")
@RequiredArgsConstructor
public class ManufacturingController {

    private final ManufacturingService manufacturingService;

    @PostMapping
    public ResponseEntity<ManufacturingProcessResponse> startProcess(
            @RequestBody ManufacturingProcessRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(manufacturingService.startProcess(request));
    }

    @GetMapping
    public ResponseEntity<List<ManufacturingProcessResponse>> getAllProcesses() {
        return ResponseEntity.ok(manufacturingService.getAllProcesses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManufacturingProcessResponse> getProcessById(@PathVariable Long id) {
        return ResponseEntity.ok(manufacturingService.getProcessById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManufacturingProcessResponse> updateProcess(
            @PathVariable Long id, @RequestBody ManufacturingProcessRequest request) {
        return ResponseEntity.ok(manufacturingService.updateProcess(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProcess(@PathVariable Long id) {
        manufacturingService.deleteProcess(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{processId}/stages/{stageId}")
    public ResponseEntity<ManufacturingStageResponse> updateStage(
            @PathVariable Long processId, @PathVariable Long stageId,
            @RequestBody ManufacturingStageUpdateRequest request) {
        return ResponseEntity.ok(
            manufacturingService.updateStageStatus(processId, stageId, request)
        );
    }

    @GetMapping("/{processId}/stages")
    public ResponseEntity<List<ManufacturingStageResponse>> getProcessStages(
            @PathVariable Long processId) {
        return ResponseEntity.ok(manufacturingService.getProcessStages(processId));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ManufacturingProcessResponse> completeProcess(@PathVariable Long id) {
        return ResponseEntity.ok(manufacturingService.completeProcess(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ManufacturingProcessResponse> cancelProcess(@PathVariable Long id) {
        return ResponseEntity.ok(manufacturingService.cancelProcess(id));
    }
}