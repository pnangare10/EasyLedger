package com.example.demo.controller;

import com.example.demo.dto.BOMRequest;
import com.example.demo.dto.BOMResponse;
import com.example.demo.services.BOMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boms")
@RequiredArgsConstructor
public class BOMController {

    private final BOMService bomService;

    @PostMapping
    public ResponseEntity<BOMResponse> createBOM(@RequestBody BOMRequest bomRequest) {
        BOMResponse bomResponse = bomService.createBOM(bomRequest);
        return new ResponseEntity<>(bomResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BOMResponse>> getAllBOMs() {
        List<BOMResponse> bomResponses = bomService.getAllBOMs();
        return new ResponseEntity<>(bomResponses, HttpStatus.OK);
    }
}