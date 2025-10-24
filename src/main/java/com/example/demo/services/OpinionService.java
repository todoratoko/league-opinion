package com.example.demo.services;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.ResponseMessage;
import com.example.demo.model.dto.AddOpinionDTO;
import com.example.demo.model.dto.CreateOpinionDTO;
import com.example.demo.model.dto.OpinionWithOwnerDTO;
import com.example.demo.model.entities.Game;
import com.example.demo.model.entities.Opinion;
import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.GameRepository;
import com.example.demo.model.repositories.OpinionSaveRepository;
import com.example.demo.model.entities.OpinionSave;
import com.example.demo.model.repositories.OpinionRepository;
import com.example.demo.model.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OpinionService {
    @Autowired
    OpinionRepository opinionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    JwtService jwtService;
    @Autowired
    OpinionSaveRepository opinionSaveRepository;


    public OpinionWithOwnerDTO getById(Long id) {
        Optional<Opinion> opinionOptional = opinionRepository.findById(id);
        if (opinionOptional.isPresent()) {
            Opinion opinion = opinionOptional.get();
            OpinionWithOwnerDTO dto = modelMapper.map(opinion, OpinionWithOwnerDTO.class);
            return dto;
        } else {
            throw new NotFoundException("Opinion not found");
        }
    }



    public OpinionWithOwnerDTO addOpinion(AddOpinionDTO opinion, Long matchId, Long id, HttpServletResponse response, HttpServletRequest request) {
        checkAndRenewToken(request, response);
        Game game = gameRepository.findById(matchId).orElseThrow(() -> new NotFoundException("There is no such Match"));
        User ownerUser = userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new NotFoundException("Owner not found"));
        Opinion opinionSave = modelMapper.map(opinion, Opinion.class);
        opinionSave.setOwner(ownerUser);
        opinionSave.setGame(game);
        opinionRepository.save(opinionSave);
        ownerUser.getOpinions().add(opinionSave);
        userRepository.save(ownerUser);
        OpinionWithOwnerDTO opinionWithOwnerDTO = modelMapper.map(opinionSave, OpinionWithOwnerDTO.class);
        return opinionWithOwnerDTO;
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

    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new UnauthorizedException("Missing or invalid Authorization header");
    }

    public List<OpinionWithOwnerDTO> getOpinionsByRegionId(Long regionId) {
        List<Opinion> opinions = opinionRepository.findByGameRegionId(regionId);
        return opinions.stream()
                .map(opinion -> modelMapper.map(opinion, OpinionWithOwnerDTO.class))
                .collect(Collectors.toList());
    }

    public List<OpinionWithOwnerDTO> getAllOpinions() {
        List<Opinion> opinions = opinionRepository.findAll();
        return opinions.stream()
                .map(opinion -> modelMapper.map(opinion, OpinionWithOwnerDTO.class))
                .collect(Collectors.toList());
    }

    public List<OpinionWithOwnerDTO> getUserOpinions(Long userId) {
        List<Opinion> opinions = opinionRepository.findByOwnerId(userId);
        return opinions.stream()
                .map(opinion -> modelMapper.map(opinion, OpinionWithOwnerDTO.class))
                .collect(Collectors.toList());
    }

    public List<OpinionWithOwnerDTO> getUserSavedOpinions(Long userId) {
        List<Opinion> opinions = opinionSaveRepository.findOpinionsByUserId(userId);
        return opinions.stream()
                .map(opinion -> modelMapper.map(opinion, OpinionWithOwnerDTO.class))
                .collect(Collectors.toList());
    }

    public ResponseMessage saveOpinion(Long opinionId, Long userId, HttpServletRequest request, HttpServletResponse response) {
        checkAndRenewToken(request, response);

        Opinion opinion = opinionRepository.findById(opinionId)
                .orElseThrow(() -> new NotFoundException("Opinion not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Check if already saved
        if (opinionSaveRepository.existsByOpinionAndUser(opinion, user)) {
            throw new BadRequestException("Opinion already saved");
        }

        OpinionSave opinionSave = new OpinionSave();
        opinionSave.setOpinion(opinion);
        opinionSave.setUser(user);
        opinionSave.setCreatedAt(java.time.LocalDateTime.now());
        opinionSaveRepository.save(opinionSave);

        return new ResponseMessage("Opinion saved successfully");
    }

    public ResponseMessage unsaveOpinion(Long opinionId, Long userId, HttpServletRequest request, HttpServletResponse response) {
        checkAndRenewToken(request, response);

        Opinion opinion = opinionRepository.findById(opinionId)
                .orElseThrow(() -> new NotFoundException("Opinion not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        OpinionSave opinionSave = opinionSaveRepository.findByOpinionAndUser(opinion, user)
                .orElseThrow(() -> new NotFoundException("Opinion not saved"));

        opinionSaveRepository.delete(opinionSave);

        return new ResponseMessage("Opinion unsaved successfully");
    }

    public OpinionWithOwnerDTO createOpinion(CreateOpinionDTO createOpinionDTO, Long userId, HttpServletRequest request, HttpServletResponse response) {
        checkAndRenewToken(request, response);

        // Validate percentages add up to 100
        if (createOpinionDTO.getTeamOnePercent() + createOpinionDTO.getTeamTwoPercent() != 100) {
            throw new BadRequestException("Team percentages must add up to 100");
        }

        // Find the game
        Game game = gameRepository.findById(createOpinionDTO.getGameId())
                .orElseThrow(() -> new NotFoundException("Game not found with ID: " + createOpinionDTO.getGameId()));

        // Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Create the opinion
        Opinion opinion = new Opinion();
        opinion.setGame(game);
        opinion.setOwner(user);
        opinion.setTeamOnePercent(createOpinionDTO.getTeamOnePercent());
        opinion.setTeamTwoPercent(createOpinionDTO.getTeamTwoPercent());
        opinion.setComment(createOpinionDTO.getComment());
        opinion.setCreatedAt(new Date());

        // Save the opinion
        Opinion savedOpinion = opinionRepository.save(opinion);

        // Add to user's opinions list
        user.getOpinions().add(savedOpinion);
        userRepository.save(user);

        // Map to DTO and return
        return modelMapper.map(savedOpinion, OpinionWithOwnerDTO.class);
    }

}
