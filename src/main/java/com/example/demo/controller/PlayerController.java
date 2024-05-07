package com.example.demo.controller;

import com.example.demo.model.entities.Player;
import com.example.demo.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @GetMapping("/player/{id}")
    public ResponseEntity<Player> getById(@PathVariable int id) {
        Player player = playerService.getById(id);;
        return ResponseEntity.ok(player);
    }
}
