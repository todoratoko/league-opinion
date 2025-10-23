package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {
    private int totalOpinions;
    private double accuracyOnMargin;
    private int followersCount;
    private int followingCount;
}
