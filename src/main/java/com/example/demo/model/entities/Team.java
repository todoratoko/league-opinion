package com.example.demo.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String name;
    @Column
    private String tag;
}
