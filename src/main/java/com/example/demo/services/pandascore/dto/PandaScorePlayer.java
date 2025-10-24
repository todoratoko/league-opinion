package com.example.demo.services.pandascore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PandaScorePlayer {

    private Long id;

    private String name;  // In-game name

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String role;  // top, jun, mid, adc, sup

    @JsonProperty("image_url")
    private String imageUrl;

    private String nationality;

    private Boolean active;

    private String slug;

    private Integer age;

    private String birthday;
}
