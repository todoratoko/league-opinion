package com.example.demo.controller;

import com.example.demo.model.entities.Team;
import com.example.demo.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamController {

    @Autowired
    TeamService teamService;

    @GetMapping("/teams/{id}")
    public ResponseEntity<Team> getById(@PathVariable int id) {
        Team team = teamService.getById(id);;
        return ResponseEntity.ok(team);
    }
}
