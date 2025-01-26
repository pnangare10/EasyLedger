package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
public class ManufacturingStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private LocalDate dueDate;
    private String status; // PENDING, IN_PROGRESS, COMPLETED
    private String description;

    @ManyToOne
    @JoinColumn(name = "process_id", nullable = false)
    private ManufacturingProcess process;
}