package com.example.demo.controller;

import com.example.demo.model.entities.Match;
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

    @GetMapping("/opinions/{id}")
    public ResponseEntity<Match> getById(@PathVariable int id) {
        Match match = matchService.getById(id);
        return ResponseEntity.ok(match);
    }
}
