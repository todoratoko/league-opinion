package com.example.demo.controller;

import com.example.demo.model.entities.Game;
import com.example.demo.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {
    @Autowired
    GameService gameService;

    @GetMapping("/game/{id}")
    public ResponseEntity<Game> getById(@PathVariable int id) {
        Game game = gameService.getById(id);
        return ResponseEntity.ok(game);
    }
}
