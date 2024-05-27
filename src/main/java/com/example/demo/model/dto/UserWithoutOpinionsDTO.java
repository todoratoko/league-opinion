package com.example.demo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserWithoutOpinionsDTO {

    private long id;
    private String username;
    private String profileImage;
}
