package com.example.demo.controller;

import com.example.demo.model.entities.Game;
import com.example.demo.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchController {
    @Autowired
    MatchService matchService;

    @GetMapping("/match/{id}")
    public ResponseEntity<Game> getById(@PathVariable int id) {
        Game game = matchService.getById(id);
        return ResponseEntity.ok(game);
    }
}
