package com.example.demo.repository;

import com.example.demo.models.ManufacturingProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturingProcessRepository extends JpaRepository<ManufacturingProcess, Long> {
}