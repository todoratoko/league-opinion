package com.example.demo.model.dto;

import jakarta.persistence.Column;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddOpinionDTO {
    @NotBlank(message = "Give percent of one team")
    private int team_one_percent;
    private int team_two_percent;

}
