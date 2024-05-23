package com.example.demo.services;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.RegisterUserDTO;
import com.example.demo.model.dto.EditUserDTO;
import com.example.demo.model.dto.UserResponseDTO;
import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;

    public UserResponseDTO login(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (username == null || username.isBlank()) {
            throw new BadRequestException("User name is mandatory");
        }
        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password field is mandatory");
        }
        User u = userRepository.findByUsernameAndPassword(username, password);
        if (u == null) {
            throw new UnauthorizedException("Wrong  credentials");
        }
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return dto;
    }

    public UserResponseDTO register(RegisterUserDTO user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String confirmPassword = user.getConfirmPassword();
        if (!password.equals(confirmPassword)) {
            throw new NotFoundException("Passwords do not match");
        }
        validateUsername(username);
        validatePassword(password);
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        userRepository.save(u);
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return dto;
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("User name is mandatory");
        }
        if (username.length() <= 5) {
            throw new BadRequestException("Username is too short, must be at least 6 symbols");
        }
        if (userRepository.findByUsername(username) != null) {
            throw new BadRequestException("User already exists");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password field is mandatory");
        }
        if (!password.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
            throw new BadRequestException("Password field requires other symbols");
        }
    }


    public UserResponseDTO getById(long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            UserResponseDTO dto = modelMapper.map(opt, UserResponseDTO.class);
            return dto;
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public UserResponseDTO edit(EditUserDTO user, long id) {
        if (user.getNewPassword() != null && user.getConfirmNewPassword() != null) {
            validatePassword(user.getNewPassword());
            if (!user.getNewPassword().equals(user.getConfirmNewPassword())) {
                throw new NotFoundException("New password does not match");
            }
        }
        if ((user.getNewPassword() == null && user.getConfirmNewPassword() != null) ||
                (user.getNewPassword() != null && user.getConfirmNewPassword() == null)) {
            throw new NotFoundException("New password does not match");
        }
        Optional<User> opt = userRepository.findById(id);
        User newUser = modelMapper.map(opt, User.class);
        if (!newUser.getPassword().equals(user.getPassword())) {
            throw new NotFoundException("Old password is wrong!");
        }
        if (user.getNewPassword() != null) {
            newUser.setPassword(user.getNewPassword());
        }
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        if (user.getImage() != null) {
            newUser.setImage(user.getImage());
        }
        if (opt.isPresent()) {
            userRepository.save(newUser);
            UserResponseDTO dto = modelMapper.map(newUser, UserResponseDTO.class);
            return dto;
        } else {
            throw new NotFoundException("User not found");
        }
    }
}


