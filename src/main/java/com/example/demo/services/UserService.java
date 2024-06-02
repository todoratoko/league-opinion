package com.example.demo.services;

import com.example.demo.controller.UserController;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entities.Opinion;
import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        User u = userRepository.findByUsername(username);
        if (u == null) {
            throw new UnauthorizedException("Wrong  credentials");
        }
        if (!passwordEncoder.matches(user.getPassword(), u.getPassword())) {
            throw new UnauthorizedException("Wrong  credentials");
        }
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return dto;
    }

    public UserResponseDTO register(RegisterUserDTO user) {
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new NotFoundException("Passwords do not match");
        }
        if (!user.getEmail().equals(user.getConfirmEmail())) {
            throw new NotFoundException("Emails do not match");
        }
        validateUsername(user.getUsername());
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        validateEmail(user.getEmail());
        User u = modelMapper.map(user, User.class);
        userRepository.save(u);
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return dto;
    }

    private void validateEmail(String email) {
        if (userRepository.findByEmail(email) != null) {
            throw new BadRequestException("Email is taken");
        }
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


    public UserWithOpinionsDTO getById(long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            User u = opt.get();
            UserWithOpinionsDTO dto = modelMapper.map(opt, UserWithOpinionsDTO.class);
            Set<Opinion> opinions = u.getOpinions();
            dto.setOpinionList(opinions.stream().
                    map(opinion -> modelMapper.map(opinion, OpinionWithoutOwnerDTO.class)).
                    collect(Collectors.toList()));
            return dto;
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public UserResponseDTO edit(EditUserDTO user, long id, HttpServletRequest request) {
        UserController.validateLogin(request);
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
        if (!passwordEncoder.matches(user.getPassword(), newUser.getPassword())) {
            throw new UnauthorizedException("Wrong  credentials");
        }
        if (user.getNewPassword() != null) {
            newUser.setPassword(user.getNewPassword());
        }
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        if (user.getProfileImage() != null) {
            newUser.setProfileImage(user.getProfileImage());
        }
        if (opt.isPresent()) {
            userRepository.save(newUser);
            UserResponseDTO dto = modelMapper.map(newUser, UserResponseDTO.class);
            return dto;
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public UserResponseDTO followUser(long followingId, long userId, HttpServletRequest request) {
        UserController.validateLogin(request);
        User user = getUserById(userId);
        User following = getUserById(followingId);
        if (user.getFollowing().contains(following)) {
            throw new NotFoundException("Already followed this user!");
        }
        if (followingId == userId) {
            throw new NotFoundException("Can not follow yourself!");
        }
        user.getFollowing().add(following);
        userRepository.save(user);
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        return dto;
    }


    public UserResponseDTO unfollowUser(long followingId, Long userId, HttpServletRequest request) {
        UserController.validateLogin(request);
        User user = getUserById(userId);
        User following = getUserById(followingId);
        if (!user.getFollowing().contains(following)) {
            throw new NotFoundException("You have to follow the user, in order to unfollow!");
        }
        user.getFollowing().remove(following);
        userRepository.save(user);
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        return dto;
    }

    // iznesi go na chitavo mqsto
    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @SneakyThrows
    public String uploadFile(MultipartFile file, HttpServletRequest request) {
        UserController.validateLogin(request);
        long loggedUserId = (long) request.getSession().getAttribute(UserController.USER_ID);
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "." + extension;
        Files.copy(file.getInputStream(), new File("uploads" + File.separator + name).toPath());
        User u = getUserById(loggedUserId);
        u.setProfileImage(name);
        userRepository.save(u);
        return name;


    }
}


