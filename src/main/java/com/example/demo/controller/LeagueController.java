package com.example.demo.controller;

import com.example.demo.model.entities.League;
import com.example.demo.model.repositories.LeagueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leagues")
@CrossOrigin(origins = "*")
public class LeagueController {

    @Autowired
    private LeagueRepository leagueRepository;

    @GetMapping
    public ResponseEntity<List<League>> getAllLeagues() {
        return ResponseEntity.ok(leagueRepository.findAll());
    }

    @GetMapping("/{code}")
    public ResponseEntity<League> getLeagueByCode(@PathVariable String code) {
        return leagueRepository.findByCode(code.toUpperCase())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
