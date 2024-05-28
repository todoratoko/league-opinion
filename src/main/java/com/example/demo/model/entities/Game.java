package com.example.demo.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name="games")
public class Game {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String matchStartDateTime; // ???
    @Column
    private int teamOneId;
    @Column
    private int teamTwoId;
    @Column
    private int teamOnePercent;
    @Column
    private int teamTwoPercent;


}
