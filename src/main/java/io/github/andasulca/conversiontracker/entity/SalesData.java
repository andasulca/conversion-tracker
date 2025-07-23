package io.github.andasulca.conversiontracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_data")
public class SalesData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingId;
    private LocalDateTime visitDate;
    private String product;
    private LocalDateTime saleDate;
    private Double salePrice;
    private Double commissionAmount;

    // Getters and setters...
}