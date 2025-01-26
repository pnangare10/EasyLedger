package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class ManufacturingProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String manufacturingId;
    private LocalDate expectedCompletionDate;
    private Integer quantityToProduce;
    private String status;
    private Boolean materialsReserved = false;

    @ManyToOne
    @JoinColumn(name = "bom_id", nullable = false)
    private BillOfMaterials bom;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
    private List<ManufacturingStage> stages;
}