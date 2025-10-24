package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "leagues")
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;  // LCK, LPL, LEC, LTA, LCP

    @Column(nullable = false)
    private String name;  // Full name (e.g., "League of Legends Champions Korea")

    @Column
    private String shortName;  // Short name (e.g., "LCK")

    @Column
    private String region;  // Korea, China, Europe, Americas, Pacific

    @Column
    private String logo;  // Logo path (e.g., "/images/leagues/lck.png")

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String website;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
