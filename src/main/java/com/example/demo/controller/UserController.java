package com.example.demo.controller;

import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entities.User;
import com.example.demo.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController extends BaseController {
    public static final String USER_ID = "user_id";
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public UserResponseDTO login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        UserResponseDTO u = userService.login(user, response, request);
        return u;
    }

    @PostMapping("/reg")
    public ResponseEntity<String> register(@RequestBody RegisterUserDTO user) {
        UserResponseDTO u = userService.register(user);
        return ResponseEntity.ok("Check your email to confirm the registration");
    }

    @GetMapping("/reg/confirm")
    public ResponseEntity<String> confirmAccount(@RequestParam("token") String token) {
        //TODO login when confirming trough Mail.
        return userService.confirmMailToken(token);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody RegisterUserDTO user) {
        userService.passwordEmailSend(user);
        return ResponseEntity.ok("Link to reset your password has been sent to your email");
    }

    @PostMapping("/forgotPassword/confirmation")
    public ResponseEntity<String> forgotPasswordHandle(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword, @RequestParam("confirmNewPassword") String confirmNewPassword) {
        userService.passwordReset(token, newPassword, confirmNewPassword);
        return ResponseEntity.ok("New password was set");
    }

    @GetMapping("/users/{id}")
    public UserWithOpinionsDTO getById(@PathVariable int id) {
        return userService.getById(id);
    }

    @PutMapping("/users/edit/{id}")
    public UserResponseDTO edit(@RequestBody EditUserDTO user, @PathVariable long id, HttpServletRequest request, HttpServletResponse response) {
        UserResponseDTO u = userService.edit(user, id, request, response);
        return u;
    }

    @PostMapping("/logout")
    public void logOut(HttpSession session) {
        session.invalidate();
    }



    @PostMapping("/users/{id}/follow")
    public UserResponseDTO followUser(@PathVariable long id, HttpServletRequest request, HttpServletResponse response) {
        return userService.followUser(id, (Long) request.getSession().getAttribute(USER_ID), request, response);
    }

    @PostMapping("/users/{id}/unfollow")
    public UserResponseDTO unfollowUser(@PathVariable long id, HttpServletRequest request, HttpServletResponse response) {
        return userService.unfollowUser(id, (Long) request.getSession().getAttribute(USER_ID), request, response);
    }

    @SneakyThrows
    @PostMapping("users/image")
    public String uploadProfileImage(@RequestParam(name = "file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        return userService.uploadFile(file, request, response);
    }

}
