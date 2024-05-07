package com.example.demo.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name="matchups")
public class Match {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String matchupStartDateTime; // ???

    @Column
    private int teamOneId;
    @Column
    private int teamTwoId;
    @Column
    private int teamOnePercent;
    @Column
    private int teamTwoPercent;


}
