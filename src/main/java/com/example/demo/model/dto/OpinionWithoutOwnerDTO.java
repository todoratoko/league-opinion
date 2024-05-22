package com.example.demo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class OpinionWithoutOwnerDTO {
    private Integer id;
    private Date created_at;
    private int team_one_percent;
    private int team_two_percent;
}
