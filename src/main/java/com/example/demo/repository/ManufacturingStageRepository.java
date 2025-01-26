package com.example.demo.repository;

import com.example.demo.models.ManufacturingStage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ManufacturingStageRepository extends JpaRepository<ManufacturingStage, Long> {
    List<ManufacturingStage> findByProcessId(Long processId);
}