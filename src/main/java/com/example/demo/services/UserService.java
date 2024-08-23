package com.example.demo.services;

import com.example.demo.controller.UserController;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entities.Opinion;
import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.ConfirmationRepository;
import com.example.demo.model.repositories.UserRepository;
import com.example.demo.model.entities.ConfirmationToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    public static final String LOGGED = "logged";
    public static final String USER_ID = "user_id";
    public static final String LOGGED_FROM = "logged_from";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConfirmationRepository confirmationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JwtService jwtService;



    public UserResponseDTO login(User user, HttpServletResponse response, HttpServletRequest request) {
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
        if(!u.isEnabled()){
            throw new UnauthorizedException("You need to confirm your account, trough mail");
        }
        request.getSession().setAttribute(LOGGED, true);
        request.getSession().setAttribute(USER_ID, u.getId());
        request.getSession().setAttribute(LOGGED_FROM, request.getRemoteAddr());
        u.setLastLogin(LocalDate.now());
        userRepository.save(u);
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        String token = jwtService.generateToken(user);
        response.setHeader("Authorization", "Bearer " + token);
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
        u.setCreatedAt(LocalDateTime.now());
        userRepository.save(u);
        ConfirmationToken confirmationToken = new ConfirmationToken(u);
        confirmationRepository.save(confirmationToken);
        emailService.sendEmailConfirmation(user.getEmail(),"Successful registration", "You have to confirm your registration: ", confirmationToken.getToken());
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

    public UserResponseDTO edit(EditUserDTO user, long id, HttpServletRequest request, HttpServletResponse response) {
        validateLogin(request);
        checkAndRenewToken(request, response);
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

    public UserResponseDTO followUser(long followingId, long userId, HttpServletRequest request, HttpServletResponse response) {
        checkAndRenewToken(request, response);
        validateLogin(request);
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




    public UserResponseDTO unfollowUser(long followingId, Long userId, HttpServletRequest request, HttpServletResponse response) {
        checkAndRenewToken(request, response);
        validateLogin(request);
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

     User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @SneakyThrows
    public String uploadFile(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        checkAndRenewToken(request, response);
        validateLogin(request);
        long loggedUserId = (long) request.getSession().getAttribute(UserController.USER_ID);
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "." + extension;
        Files.copy(file.getInputStream(), new File("uploads" + File.separator + name).toPath());
        User u = getUserById(loggedUserId);
        u.setProfileImage(name);
        userRepository.save(u);
        return name;
    }

    public ResponseEntity<String> confirmMailToken(String token) {
        ConfirmationToken confirmationToken = confirmationRepository.findByToken(token);
        if(confirmationToken != null){
            User user = confirmationToken.getUser();
            user.setEnabled(true);
            userRepository.save(user);
            confirmationRepository.delete(confirmationToken);
            return ResponseEntity.ok("Account verified successfully!");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token.");
        }


    }

    public void passwordEmailSend(RegisterUserDTO user) {
        User foundUser = userRepository.findByEmail(user.getEmail());
        if(foundUser == null){
            throw new NotFoundException("User not found");
        }
        ConfirmationToken confirmationToken = new ConfirmationToken(foundUser);
        confirmationRepository.save(confirmationToken);
        emailService.sendEmailForgotPassword(foundUser,confirmationToken.getToken());
    }


    public void passwordReset(String token, String newPassword, String confirmNewPassword) {
        validatePassword(newPassword);
        if(!newPassword.equals(confirmNewPassword)){
            throw new BadRequestException("Passwords miss match");
        }
        ConfirmationToken confirmationToken = confirmationRepository.findByToken(token);
        if(confirmationToken == null){
            throw new NotFoundException("Token not found");
        }
        User user = userRepository.findByEmail(confirmationToken.getUser().getEmail());
        if(user == null){
            throw new NotFoundException("User not found to validate");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        confirmationRepository.delete(confirmationToken);
    }

    public static void validateLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        boolean newSession = session.isNew();
        boolean logged = session.getAttribute(LOGGED) != null && ((Boolean) session.getAttribute(LOGGED));
        boolean sameIp = request.getRemoteAddr().equals(session.getAttribute(LOGGED_FROM));
        if (newSession || !logged || !sameIp) {
            throw new UnauthorizedException("You have to log in!");
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new UnauthorizedException("Missing or invalid Authorization header");
    }

    private void checkAndRenewToken(HttpServletRequest request, HttpServletResponse response) {
        String token = extractTokenFromRequest(request);
        String newToken = jwtService.isTokenValidAndRenew(token);
        if (newToken != null) {
            response.setHeader("Authorization", "Bearer " + newToken);
        } else {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

}


