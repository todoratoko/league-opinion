package com.example.demo.model.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private String name;  // Short name (e.g., "T1")
    @Column
    private String fullName;  // Full team name (e.g., "SK Telecom T1")
    @Column
    private String tag;  // Team tag/code
    @Column
    private String region;  // Region string
    @Column
    private String country;  // Country name
    @Column
    private String image;  // Team logo URL
    @Column
    private String twitter;  // Twitter handle
    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Player> players;
}
