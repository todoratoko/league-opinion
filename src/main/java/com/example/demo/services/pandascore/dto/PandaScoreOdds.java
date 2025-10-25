package com.example.demo.services.pandascore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO for PandaScore betting odds
 * Note: This requires a premium PandaScore subscription to access
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PandaScoreOdds {

    private Long id;

    @JsonProperty("match_id")
    private Long matchId;

    @JsonProperty("market_id")
    private Long marketId;

    @JsonProperty("market_name")
    private String marketName;  // e.g., "match_winner", "first_blood", etc.

    private List<PandaScoreOddsOption> options;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    private String status;  // "active", "suspended", "settled"
}
