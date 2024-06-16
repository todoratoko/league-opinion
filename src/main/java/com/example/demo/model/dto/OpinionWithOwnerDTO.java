package com.example.demo.model.dto;

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
    private long gameId;
    private String  opinion;
    private UserWithoutOpinionsDTO owner;

}
