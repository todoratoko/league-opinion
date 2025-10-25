package com.example.demo.services.pandascore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for individual odds options (e.g., Team 1 to win, Team 2 to win)
 * Note: This requires a premium PandaScore subscription to access
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PandaScoreOddsOption {

    private String name;  // e.g., "Team 1", "Team 2", "Over 2.5", etc.

    @JsonProperty("opponent_id")
    private Long opponentId;  // ID of the team/opponent this odd is for

    private Double odd;  // Decimal odds (e.g., 1.75, 2.10)

    @JsonProperty("implied_probability")
    private Double impliedProbability;  // Probability as percentage (0-100)

    private Boolean available;  // Whether this option is currently available for betting
}
