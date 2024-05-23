package com.example.demo.model.dto;

import lombok.Data;


@Data
public class AddOpinionDTO {
    private int team_one_percent;
    private int team_two_percent;
    private long ownerId;

}
