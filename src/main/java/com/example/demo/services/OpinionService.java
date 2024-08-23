package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.AddOpinionDTO;
import com.example.demo.model.dto.OpinionWithOwnerDTO;
import com.example.demo.model.entities.Game;
import com.example.demo.model.entities.Opinion;
import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.GameRepository;
import com.example.demo.model.repositories.OpinionRepository;
import com.example.demo.model.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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


}
