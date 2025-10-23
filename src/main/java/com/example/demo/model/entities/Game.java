package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="games")
public class Game {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String matchStartDateTime;
    @Column
    private int teamOneId;
    @Column
    private int teamTwoId;
    @Column
    private int teamOnePercent;
    @Column
    private int teamTwoPercent;
    @Column
    private Integer teamOneScore;  // Final score (for past matches)
    @Column
    private Integer teamTwoScore;  // Final score (for past matches)
    @Column
    private String tournament;  // Tournament name (e.g., "LCK", "Worlds")
    @Column
    private String matchType;  // Match type (e.g., "Bo1", "Bo3", "Bo5")
    @Column
    private Boolean isFinished;  // Whether match is completed

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

}
