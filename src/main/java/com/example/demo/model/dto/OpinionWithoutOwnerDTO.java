package com.example.demo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class OpinionWithoutOwnerDTO {
    private long id;
    private Date createdAt;
    private int teamOnePercent;
    private int teamTwoPercent;
    private String  opinion;

}
