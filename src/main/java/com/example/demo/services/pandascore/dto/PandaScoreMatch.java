package com.example.demo.services.pandascore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PandaScoreMatch {

    private Long id;

    private String name;

    @JsonProperty("scheduled_at")
    private OffsetDateTime scheduledAt;

    @JsonProperty("begin_at")
    private OffsetDateTime beginAt;

    @JsonProperty("end_at")
    private OffsetDateTime endAt;

    private String status;  // "not_started", "running", "finished"

    @JsonProperty("match_type")
    private String matchType;  // "best_of"

    @JsonProperty("number_of_games")
    private Integer numberOfGames;  // 1, 3, 5 for Bo1, Bo3, Bo5

    private List<PandaScoreOpponent> opponents;

    private PandaScoreTournament tournament;

    private PandaScoreSerie serie;

    private PandaScoreLeague league;

    private List<PandaScoreGame> games;

    private List<PandaScoreResults> results;

    @JsonProperty("videogame")
    private PandaScoreVideogame videogame;
}
