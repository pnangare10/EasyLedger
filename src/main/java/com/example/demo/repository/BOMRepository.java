package com.example.demo.repository;

import com.example.demo.models.BillOfMaterials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BOMRepository extends JpaRepository<BillOfMaterials, Long> {
}