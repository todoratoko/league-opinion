package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;  // worlds-2025, msi-2025, first-stand

    @Column(nullable = false)
    private String name;  // Full name (e.g., "2025 Season World Championship")

    @Column
    private String shortName;  // Short name (e.g., "Worlds 2025")

    @Column
    private Integer year;  // 2025

    @Column
    private String logo;  // Logo path (e.g., "/images/tournaments/worlds-2025.png")

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private String location;  // China, Vancouver, etc.

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
