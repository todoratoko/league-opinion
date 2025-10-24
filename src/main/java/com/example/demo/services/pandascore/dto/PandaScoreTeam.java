package com.example.demo.services.pandascore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PandaScoreTeam {

    private Long id;

    private String name;

    private String acronym;  // Team tag (T1, GEN, G2, etc.)

    @JsonProperty("image_url")
    private String imageUrl;

    private String location;  // Country/Region

    @JsonProperty("slug")
    private String slug;

    private List<PandaScorePlayer> players;  // Team roster
}
