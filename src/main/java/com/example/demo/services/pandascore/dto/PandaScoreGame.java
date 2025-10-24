package com.example.demo.services.pandascore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PandaScoreGame {

    private Long id;

    @JsonProperty("begin_at")
    private OffsetDateTime beginAt;

    @JsonProperty("end_at")
    private OffsetDateTime endAt;

    private Boolean finished;

    private Integer length;  // Game duration in seconds

    private Integer position;  // Game number in the series

    private String status;

    @JsonProperty("winner_id")
    private Long winnerId;
}
