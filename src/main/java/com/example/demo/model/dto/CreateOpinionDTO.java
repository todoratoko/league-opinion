package com.example.demo.model.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CreateOpinionDTO {
    @NotNull(message = "Game ID is required")
    private Long gameId;

    @NotNull(message = "Team one percentage is required")
    @Min(value = 0, message = "Percentage must be at least 0")
    @Max(value = 100, message = "Percentage must be at most 100")
    private Integer teamOnePercent;

    @NotNull(message = "Team two percentage is required")
    @Min(value = 0, message = "Percentage must be at least 0")
    @Max(value = 100, message = "Percentage must be at most 100")
    private Integer teamTwoPercent;

    private String comment;
}
