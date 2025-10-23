package com.example.demo.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name")
    private String name;  // Player name/game tag
    @Column
    private String nickname;  // Optional nickname
    @Column
    private String role;  // Top, Jungle, Mid, ADC, Support
    @Column
    private String image;  // Player image URL
    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonBackReference
    private Team team;
}
