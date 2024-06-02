package com.example.demo.controller;

import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entities.User;
import com.example.demo.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController extends BaseController {
    public static final String LOGGED = "logged";
    public static final String USER_ID = "user_id";
    public static final String LOGGED_FROM = "logged_from";
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public UserResponseDTO login(@RequestBody User user, HttpSession session, HttpServletRequest request) {
        UserResponseDTO u = userService.login(user);
        session.setAttribute(LOGGED, true);
        session.setAttribute(USER_ID, u.getId());
        session.setAttribute(LOGGED_FROM, request.getRemoteAddr());
        return u;
    }

    @PostMapping("/reg")
    public UserResponseDTO register(@RequestBody RegisterUserDTO user) {
        //TODO direct login when register. Or login when confirming trough Mail.
        UserResponseDTO u = userService.register(user);
        return u;
    }

    @GetMapping("/users/{id}")
    public UserWithOpinionsDTO getById(@PathVariable int id) {
        return userService.getById(id);
    }

    @PutMapping("/users/edit/{id}")
    public UserResponseDTO edit(@RequestBody EditUserDTO user, @PathVariable long id, HttpSession session, HttpServletRequest request) {
        validateLogin(session, request);
        UserResponseDTO u = userService.edit(user, id);
        return u;
    }

    @PostMapping("/logout")
    public void logOut(HttpSession session) {
        session.invalidate();
    }

    private void validateLogin(HttpSession session, HttpServletRequest request) {
        boolean newSession = session.isNew();
        boolean logged = session.getAttribute(LOGGED) != null && ((Boolean) session.getAttribute(LOGGED));
        boolean sameIp = request.getRemoteAddr().equals(session.getAttribute(LOGGED_FROM));
        if (newSession || !logged || !sameIp) {
            throw new UnauthorizedException("You have to log in!");
        }
    }

    @PostMapping("/users/{id}/follow")
    public UserResponseDTO followUser(@PathVariable long id, HttpSession session, HttpServletRequest request){
        validateLogin(session, request);
        return userService.followUser(id, (Long) session.getAttribute(USER_ID));
    }
    @PostMapping("/users/{id}/unfollow")
    public UserResponseDTO unfollowUser(@PathVariable long id, HttpSession session, HttpServletRequest request){
        validateLogin(session, request);
        return userService.unfollowUser(id, (Long) session.getAttribute(USER_ID));
    }

}
