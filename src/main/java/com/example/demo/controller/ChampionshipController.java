package com.example.demo.controller;

import com.example.demo.model.entities.Championship;
import com.example.demo.services.ChampionshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChampionshipController {
    @Autowired
    ChampionshipService championshipService;

    @GetMapping("/championships/{id}")
    public ResponseEntity<Championship> getById(int id){
        Championship championship = championshipService.getById(id);
        return ResponseEntity.ok(championship);

    }

}
