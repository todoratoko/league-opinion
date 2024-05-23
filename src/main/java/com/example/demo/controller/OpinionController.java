package com.example.demo.controller;

import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.AddOpinionDTO;
import com.example.demo.model.dto.OpinionWithOwnerDTO;
import com.example.demo.model.entities.Opinion;
import com.example.demo.services.OpinionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@Validated
public class OpinionController extends BaseController{

    public static final String LOGGED = "logged";
    @Autowired
    private OpinionService opinionService;


    @GetMapping("/opinions/{id}")
    public OpinionWithOwnerDTO getById(@PathVariable int id) {
        OpinionWithOwnerDTO dto = opinionService.getById(id);
        return dto;
    }
    @PostMapping("/opinions")
    public OpinionWithOwnerDTO add(@Valid @RequestBody AddOpinionDTO opinion, HttpSession session){
        validateLogin(session);
        return opinionService.addOpinion(opinion, (Integer) session.getAttribute(UserController.USER_ID));
    }




    private void validateLogin(HttpSession session) {
        if(session.isNew()|| !(Boolean)session.getAttribute(LOGGED)){
            throw new UnauthorizedException("You have to log in!");
        }
    }
}
