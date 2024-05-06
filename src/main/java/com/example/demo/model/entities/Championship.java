package com.example.demo.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "championships")
public class Championship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column
    private String tag;
    @Column
    private String region;
}
