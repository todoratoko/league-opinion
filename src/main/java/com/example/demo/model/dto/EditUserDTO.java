package com.example.demo.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EditUserDTO {
    @NotBlank
    private String password;
    private String newPassword;
    private String confirmNewPassword;
    private String email;
    private String profileImage;
}
