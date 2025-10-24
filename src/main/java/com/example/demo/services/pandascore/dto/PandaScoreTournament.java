package com.example.demo.services.pandascore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PandaScoreTournament {

    private Long id;

    private String name;

    private String slug;

    @JsonProperty("begin_at")
    private OffsetDateTime beginAt;

    @JsonProperty("end_at")
    private OffsetDateTime endAt;

    @JsonProperty("serie_id")
    private Long serieId;

    @JsonProperty("league_id")
    private Long leagueId;
}
