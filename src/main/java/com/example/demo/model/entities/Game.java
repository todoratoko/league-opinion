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

    // PandaScore integration fields
    @Column(name = "pandascore_match_id", unique = true)
    private Long pandascoreMatchId;  // PandaScore match ID for syncing

    @Column(name = "match_status")
    private String matchStatus;  // "not_started", "running", "finished"

    @Column(name = "league_name")
    private String leagueName;  // League name from PandaScore

    @Column(name = "league_code")
    private String leagueCode;  // League code (e.g., "LCK", "WORLDS", "EMEA")

    @Column(name = "serie_name")
    private String serieName;  // Serie/season name from PandaScore

}
