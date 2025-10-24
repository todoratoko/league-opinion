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
    @Column(name = "pandascore_player_id", unique = true)
    private Long pandascorePlayerId;  // PandaScore player ID for syncing
    @Column
    private String firstName;  // Player's first name
    @Column
    private String lastName;  // Player's last name
    @Column
    private String nationality;  // Player's nationality (country code)
    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonBackReference
    private Team team;
}
