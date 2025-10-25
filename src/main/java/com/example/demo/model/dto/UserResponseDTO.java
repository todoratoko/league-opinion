package com.example.demo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserResponseDTO {
    private long id;
    private String username;
    private String email;
    private String profileImage;
    private Double portfolioSize;
    private Double minEdge;
    private Double minWin;
}
