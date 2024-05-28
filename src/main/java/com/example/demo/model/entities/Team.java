package com.example.demo.model.entities;

import jakarta.persistence.*;

import java.util.Set;

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
    @OneToMany(mappedBy = "team")
    private Set<Player> players;
}
