package com.example.demo.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String gameTag;
    @Column
    private String firstName;
    @Column
    private String lastName;

}
