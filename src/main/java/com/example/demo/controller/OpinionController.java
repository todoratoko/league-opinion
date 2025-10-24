package com.example.demo.controller;

import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.AddOpinionDTO;
import com.example.demo.model.dto.CreateOpinionDTO;
import com.example.demo.model.dto.OpinionWithOwnerDTO;
import com.example.demo.model.dto.ResponseMessage;
import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.UserRepository;
import com.example.demo.services.JwtService;
import com.example.demo.services.OpinionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@Validated
public class OpinionController extends BaseController{

    public static final String LOGGED = "logged";
    public static final String LOGGED_FROM = "logged_from";
    @Autowired
    private OpinionService opinionService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/opinions/{id}")
    public OpinionWithOwnerDTO getById(@PathVariable long id) {
        OpinionWithOwnerDTO dto = opinionService.getById(id);
        return dto;
    }

    @GetMapping("/opinions")
    public List<OpinionWithOwnerDTO> getOpinions(@RequestParam(required = false) Long regionId) {
        if (regionId != null) {
            return opinionService.getOpinionsByRegionId(regionId);
        } else {
            return opinionService.getAllOpinions();
        }
    }

    @GetMapping("/opinions/region/{regionId}")
    public List<OpinionWithOwnerDTO> getOpinionsByRegion(@PathVariable Long regionId) {
        return opinionService.getOpinionsByRegionId(regionId);
    }

    @PostMapping("/opinions")
    public OpinionWithOwnerDTO createOpinion(@Valid @RequestBody CreateOpinionDTO createOpinionDTO,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        Long userId = getUserIdFromSessionOrToken(null, request);
        return opinionService.createOpinion(createOpinionDTO, userId, request, response);
    }

    @PostMapping("/match/opinions/{matchId}")
    public OpinionWithOwnerDTO add(@Valid @RequestBody AddOpinionDTO opinion, @PathVariable long matchId, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        validateLogin(session, request);
        return opinionService.addOpinion(opinion, matchId, (Long) session.getAttribute(UserController.USER_ID), response, request);
    }

    @PostMapping("/opinions/{opinionId}/save")
    public ResponseMessage saveOpinion(@PathVariable long opinionId, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        Long userId = getUserIdFromSessionOrToken(session, request);
        return opinionService.saveOpinion(opinionId, userId, request, response);
    }

    @DeleteMapping("/opinions/{opinionId}/save")
    public ResponseMessage unsaveOpinion(@PathVariable long opinionId, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        Long userId = getUserIdFromSessionOrToken(session, request);
        return opinionService.unsaveOpinion(opinionId, userId, request, response);
    }

    private Long getUserIdFromSessionOrToken(HttpSession session, HttpServletRequest request) {
        // Try to get user ID from session first (backward compatibility)
        if (session != null && !session.isNew() && session.getAttribute(LOGGED) != null && ((Boolean) session.getAttribute(LOGGED))) {
            Long userId = (Long) session.getAttribute(UserController.USER_ID);
            if (userId != null) {
                return userId;
            }
        }

        // If session doesn't work, try to extract user ID from JWT token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String username = jwtService.extractUsername(token);
                User user = userRepository.findByUsername(username);
                if (user != null) {
                    return user.getId();
                }
            } catch (Exception e) {
                // JWT parsing failed, fall through to unauthorized
            }
        }

        throw new UnauthorizedException("You have to log in!");
    }

    private void validateLogin(HttpSession session, HttpServletRequest request) {
        boolean newSession = session.isNew();
        boolean logged = session.getAttribute(LOGGED) != null && ((Boolean) session.getAttribute(LOGGED));
        boolean sameIp = request.getRemoteAddr().equals(session.getAttribute(LOGGED_FROM));
        if (newSession || !logged || !sameIp) {
            throw new UnauthorizedException("You have to log in!");
        }
    }
}
