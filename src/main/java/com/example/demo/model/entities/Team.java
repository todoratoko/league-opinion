package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column
    private String tag;
    @Column
    private String region;
    @Column
    private String image;
    @Column
    private String twitter;
    @OneToMany(mappedBy = "team")
    private Set<Player> players;
}
