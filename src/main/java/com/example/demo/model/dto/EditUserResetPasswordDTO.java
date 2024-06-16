package com.example.demo.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EditUserResetPasswordDTO {
    private String username;
   @NotBlank
    private String newPassword;
    private String confirmNewPassword;
}
