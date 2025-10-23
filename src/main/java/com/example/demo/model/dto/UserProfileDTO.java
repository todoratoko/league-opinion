package com.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileDTO {
    private long id;
    private String username;
    private String email;  // Only included for own profile
    private String profileImage;
    private LocalDateTime createdAt;
    private UserStatisticsDTO statistics;

    @JsonProperty("isFollowing")
    private boolean isFollowing;  // Whether the current user is following this profile
}
