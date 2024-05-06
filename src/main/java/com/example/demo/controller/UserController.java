package com.example.demo.controller;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.RegisterUserDTO;
import com.example.demo.model.dto.UserResponseDTO;
import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.UserRepository;
import com.example.demo.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController extends BaseController {
    public static final String LOGGED = "logged";
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody User user, HttpSession session) {
        String username = user.getUsername();
        String password = user.getPassword();
        User u = userService.login(username, password);
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        session.setAttribute(LOGGED, true);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/reg")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterUserDTO user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String confirmPassword = user.getConfirmPassword();
        //TODO direct login when register. Or login when confirming trough Mail.
        User u = userService.register(username, password, confirmPassword);
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable int id, HttpSession session) {
        User u = userService.getById(id);
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/users")
    public ResponseEntity<UserResponseDTO> register(@RequestBody User user, HttpSession session) {
        validateLogin(session);
        String username = user.getUsername();
        String password = user.getPassword();
        //TODO direct login when register. Or login when confirming trough Mail.
        User u = userService.edit(user);
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    private void validateLogin(HttpSession session) {
        if(session.isNew()|| !(Boolean)session.getAttribute(LOGGED)){
            throw new UnauthorizedException("You have to log in!");
        }
    }


}
