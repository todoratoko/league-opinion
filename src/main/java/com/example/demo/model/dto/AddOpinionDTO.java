package com.example.demo.model.dto;

import com.example.demo.model.entities.Game;
import lombok.Data;


@Data
public class AddOpinionDTO {
    private int teamOnePercent;
    private int teamTwoPercent;
    private Game game;

}
