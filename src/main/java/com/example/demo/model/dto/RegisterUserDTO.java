package com.example.demo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterUserDTO {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String confirmEmail;
}
