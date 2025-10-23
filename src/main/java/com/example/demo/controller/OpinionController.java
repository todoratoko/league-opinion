package com.example.demo.controller;

import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.AddOpinionDTO;
import com.example.demo.model.dto.OpinionWithOwnerDTO;
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
    @PostMapping("/match/opinions/{matchId}")
    public OpinionWithOwnerDTO add(@Valid @RequestBody AddOpinionDTO opinion, @PathVariable long matchId, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        validateLogin(session, request);
        return opinionService.addOpinion(opinion, matchId, (Long) session.getAttribute(UserController.USER_ID), response, request);
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
