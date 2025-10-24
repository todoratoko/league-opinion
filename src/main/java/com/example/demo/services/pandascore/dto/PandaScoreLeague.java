package com.example.demo.services.pandascore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PandaScoreLeague {

    private Long id;

    private String name;

    private String slug;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("url")
    private String url;
}
