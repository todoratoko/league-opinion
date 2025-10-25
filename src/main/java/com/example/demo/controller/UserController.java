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
    public ResponseEntity<String> forgotPasswordHandle(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.passwordReset(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword(), resetPasswordDTO.getConfirmNewPassword());
        return ResponseEntity.ok("New password was set");
    }

    @GetMapping("/users/{id}")
    public UserWithOpinionsDTO getById(@PathVariable int id) {
        return userService.getById(id);
    }

    // GET /users/{id}/profile - Get user profile with statistics
    @GetMapping("/users/{id}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable long id, HttpServletRequest request) {
        return ResponseEntity.ok(userService.getUserProfile(id, request));
    }

    // PUT /users/{id}/portfolio - Update user portfolio settings
    @PutMapping("/users/{id}/portfolio")
    public ResponseEntity<UserResponseDTO> updatePortfolioSettings(
            @PathVariable long id,
            @RequestBody PortfolioSettingsRequest portfolioRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        User updatedUser = userService.updatePortfolioSettings(
                id,
                portfolioRequest.getPortfolioSize(),
                portfolioRequest.getMinEdge(),
                portfolioRequest.getMinWin(),
                request,
                response
        );
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(updatedUser.getId());
        dto.setUsername(updatedUser.getUsername());
        dto.setEmail(updatedUser.getEmail());
        dto.setProfileImage(updatedUser.getProfileImage());
        dto.setPortfolioSize(updatedUser.getPortfolioSize());
        dto.setMinEdge(updatedUser.getMinEdge());
        dto.setMinWin(updatedUser.getMinWin());
        return ResponseEntity.ok(dto);
    }

    //add get all users

    @PutMapping("/users/edit/{id}")
    public UserResponseDTO edit(@RequestBody EditUserDTO user, @PathVariable long id, HttpServletRequest request, HttpServletResponse response) {
        UserResponseDTO u = userService.edit(user, id, request, response);
        return u;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logOut(HttpSession session, HttpServletRequest request) {
        try {
            // Invalidate the session
            session.invalidate();
            // Note: JWT tokens are stateless, so the frontend must delete the token from localStorage
            return ResponseEntity.ok("Logged out successfully. Please clear your authentication token.");
        } catch (IllegalStateException e) {
            // Session was already invalidated
            return ResponseEntity.ok("Already logged out");
        }
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

    // GET /users/{id}/opinions - Get all opinions created by user
    @GetMapping("/users/{id}/opinions")
    public ResponseEntity<?> getUserOpinions(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserOpinions(id));
    }

    // GET /users/{id}/saved-opinions - Get all opinions saved by user
    @GetMapping("/users/{id}/saved-opinions")
    public ResponseEntity<?> getUserSavedOpinions(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserSavedOpinions(id));
    }

    // ========== USERNAME-BASED ENDPOINTS (Pretty URLs) ==========

    // GET /users/username/{username}/profile - Get user profile by username (pretty URL)
    @GetMapping("/users/username/{username}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfileByUsername(@PathVariable String username, HttpServletRequest request) {
        return ResponseEntity.ok(userService.getUserProfileByUsername(username, request));
    }

    // GET /users/username/{username}/opinions - Get user opinions by username (pretty URL)
    @GetMapping("/users/username/{username}/opinions")
    public ResponseEntity<?> getUserOpinionsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserOpinionsByUsername(username));
    }

    // GET /users/username/{username}/saved-opinions - Get saved opinions by username (pretty URL)
    @GetMapping("/users/username/{username}/saved-opinions")
    public ResponseEntity<?> getUserSavedOpinionsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserSavedOpinionsByUsername(username));
    }

}
