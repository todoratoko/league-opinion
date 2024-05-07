package com.example.demo.controller;

import com.example.demo.model.entities.Opinion;
import com.example.demo.services.OpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpinionController extends BaseController{

    @Autowired
    private OpinionService opinionService;


    @GetMapping("/opinions/{id}")
    public ResponseEntity<Opinion> getById(@PathVariable int id) {
        Opinion opinion = opinionService.getById(id);
        return ResponseEntity.ok(opinion);
    }




}
