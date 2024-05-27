package com.example.demo.model.dto;

import com.example.demo.model.entities.Opinion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@NoArgsConstructor
public class UserWithOpinionsDTO {
    private long id;
    private String username;
    private String profileImage;
    private List<OpinionWithoutOwnerDTO> opinionList;
}
