package com.example.demo.services;

import com.example.demo.controller.UserController;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entities.Game;
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
import java.util.List;
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
    @Autowired
    private OpinionService opinionService;



    public UserResponseDTO login(User user, HttpServletResponse response, HttpServletRequest request) {
        String usernameOrEmail = user.getUsername();
        String password = user.getPassword();
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            throw new BadRequestException("Username or email is mandatory");
        }
        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password field is mandatory");
        }
        // Find user by username OR email in a single query
        User u = userRepository.findByUsernameOrEmail(usernameOrEmail);
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
            throw new BadRequestException("Passwords do not match");
        }
        if (!user.getEmail().equals(user.getConfirmEmail())) {
            throw new BadRequestException("Emails do not match");
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
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is mandatory");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new BadRequestException("Invalid email format");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new BadRequestException("Email is already taken");
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

    public UserProfileDTO getUserProfile(long id, HttpServletRequest request) {
        User user = getUserById(id);

        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setProfileImage(user.getProfileImage());
        dto.setCreatedAt(user.getCreatedAt());

        // Only include email, portfolioSize, minEdge, and minWin if viewing own profile
        Long loggedUserId = (Long) request.getSession().getAttribute(USER_ID);
        if (loggedUserId != null && loggedUserId == id) {
            dto.setEmail(user.getEmail());
            dto.setPortfolioSize(user.getPortfolioSize());
            dto.setMinEdge(user.getMinEdge());
            dto.setMinWin(user.getMinWin());
        }

        // Check if current user is following this profile
        boolean isFollowing = false;
        if (loggedUserId != null && loggedUserId != id) {
            try {
                User currentUser = getUserById(loggedUserId);
                isFollowing = currentUser.getFollowing().contains(user);
            } catch (Exception e) {
                // User not found or error, isFollowing stays false
            }
        }
        dto.setFollowing(isFollowing);

        // Calculate and set statistics
        UserStatisticsDTO statistics = calculateUserStatistics(user);
        dto.setStatistics(statistics);

        return dto;
    }

    /**
     * Update user's portfolio settings
     * @param userId The user's ID
     * @param portfolioSize The new portfolio size
     * @param minEdge The new minimum edge threshold
     * @param minWin The new minimum win percentage
     * @param request The HTTP request for validation
     * @param response The HTTP response for token renewal
     * @return Updated user
     */
    public User updatePortfolioSettings(Long userId, Double portfolioSize, Double minEdge, Double minWin,
                                       HttpServletRequest request, HttpServletResponse response) {
        // Validate authentication
        validateLogin(request);
        checkAndRenewToken(request, response);

        // Verify the logged-in user is updating their own portfolio
        Long loggedUserId = (Long) request.getSession().getAttribute(USER_ID);
        if (loggedUserId == null || !loggedUserId.equals(userId)) {
            throw new UnauthorizedException("You can only update your own portfolio settings");
        }

        // Validate input
        if (portfolioSize == null) {
            throw new BadRequestException("Portfolio size is required");
        }
        if (portfolioSize < 0) {
            throw new BadRequestException("Portfolio size must be non-negative");
        }
        if (minEdge == null) {
            throw new BadRequestException("Minimum edge is required");
        }
        if (minEdge < 0 || minEdge > 100) {
            throw new BadRequestException("Minimum edge must be between 0 and 100");
        }
        if (minWin == null) {
            throw new BadRequestException("Minimum win is required");
        }
        if (minWin < 0 || minWin > 100) {
            throw new BadRequestException("Minimum win must be between 0 and 100");
        }

        // Get and update user
        User user = getUserById(userId);
        user.setPortfolioSize(portfolioSize);
        user.setMinEdge(minEdge);
        user.setMinWin(minWin);

        return userRepository.save(user);
    }

    private UserStatisticsDTO calculateUserStatistics(User user) {
        Set<Opinion> opinions = user.getOpinions();
        int totalOpinions = opinions.size();

        // Calculate accuracy on finished games
        double accuracy = 0.0;
        if (totalOpinions > 0) {
            long correctPredictions = opinions.stream()
                .filter(opinion -> opinion.getGame().getIsFinished() != null && opinion.getGame().getIsFinished())
                .filter(this::isPredictionCorrect)
                .count();

            long finishedGamesCount = opinions.stream()
                .filter(opinion -> opinion.getGame().getIsFinished() != null && opinion.getGame().getIsFinished())
                .count();

            if (finishedGamesCount > 0) {
                accuracy = (correctPredictions * 100.0) / finishedGamesCount;
            }
        }

        // Calculate followers and following counts
        int followersCount = user.getFollowers() != null ? user.getFollowers().size() : 0;
        int followingCount = user.getFollowing() != null ? user.getFollowing().size() : 0;

        return new UserStatisticsDTO(totalOpinions, accuracy, followersCount, followingCount);
    }

    private boolean isPredictionCorrect(Opinion opinion) {
        Game game = opinion.getGame();

        // Check if game has scores
        if (game.getTeamOneScore() == null || game.getTeamTwoScore() == null) {
            return false;
        }

        // Determine predicted winner (team with higher percentage)
        boolean predictedTeamOne = opinion.getTeamOnePercent() > opinion.getTeamTwoPercent();

        // Determine actual winner (team with higher score)
        boolean actualTeamOne = game.getTeamOneScore() > game.getTeamTwoScore();

        // Check if prediction matches actual result
        return predictedTeamOne == actualTeamOne;
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
            newUser.setPassword(passwordEncoder.encode(user.getNewPassword()));
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
        // Delete old confirmation tokens to prevent unique constraint violation
        confirmationRepository.deleteByUser(foundUser);

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

    public List<?> getUserOpinions(Long userId) {
        return opinionService.getUserOpinions(userId);
    }

    public List<?> getUserSavedOpinions(Long userId) {
        return opinionService.getUserSavedOpinions(userId);
    }

    // ========== USERNAME-BASED METHODS (Pretty URLs) ==========

    public UserProfileDTO getUserProfileByUsername(String username, HttpServletRequest request) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        return getUserProfile(user.getId(), request);
    }

    public List<?> getUserOpinionsByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        return opinionService.getUserOpinions(user.getId());
    }

    public List<?> getUserSavedOpinionsByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        return opinionService.getUserSavedOpinions(user.getId());
    }

}


