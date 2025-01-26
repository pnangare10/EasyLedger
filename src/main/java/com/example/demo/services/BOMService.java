package com.example.demo.services;

import com.example.demo.dto.BOMRequest;
import com.example.demo.dto.BOMResponse;
import java.util.List;

public interface BOMService {
    BOMResponse createBOM(BOMRequest bomRequest);
    List<BOMResponse> getAllBOMs();
    BOMResponse getBOMById(Long id);
    BOMResponse updateBOM(Long id, BOMRequest bomRequest);
    void deleteBOM(Long id);
}