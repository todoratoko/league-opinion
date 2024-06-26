package com.example.demo.model.entities;

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
    @Column
    private String gameTag;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

}
