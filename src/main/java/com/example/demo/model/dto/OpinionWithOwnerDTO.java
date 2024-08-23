package com.example.demo.model.dto;

import com.example.demo.model.entities.Game;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
public class OpinionWithOwnerDTO {
    private long id;
    private Date createdAt;
    private int teamOnePercent;
    private int teamTwoPercent;
    private String comment;
    private Game game;
    private UserWithoutOpinionsDTO owner;

}
