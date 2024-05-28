package com.example.demo.controller;

import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.AddOpinionDTO;
import com.example.demo.model.dto.OpinionWithOwnerDTO;
import com.example.demo.services.OpinionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@Validated
public class OpinionController extends BaseController{

    public static final String LOGGED = "logged";
    public static final String LOGGED_FROM = "logged_from";
    @Autowired
    private OpinionService opinionService;


    @GetMapping("/opinions/{id}")
    public OpinionWithOwnerDTO getById(@PathVariable int id) {
        OpinionWithOwnerDTO dto = opinionService.getById(id);
        return dto;
    }
    @PostMapping("/opinions")
    public OpinionWithOwnerDTO add(@Valid @RequestBody AddOpinionDTO opinion, HttpSession session, HttpServletRequest request){
        validateLogin(session, request);
        return opinionService.addOpinion(opinion, (Long) session.getAttribute(UserController.USER_ID));
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
