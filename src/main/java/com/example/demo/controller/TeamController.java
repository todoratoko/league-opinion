package com.example.demo.controller;

import com.example.demo.model.entities.Game;
import com.example.demo.model.entities.Team;
import com.example.demo.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TeamController {

    @Autowired
    TeamService teamService;

    // GET /teams - Get all teams
    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    // GET /teams/{id} - Get specific team with players
    @GetMapping("/teams/{id}")
    public ResponseEntity<Team> getById(@PathVariable int id) {
        Team team = teamService.getById(id);
        return ResponseEntity.ok(team);
    }

    // GET /teams/{id}/matches - Get all matches for a team
    @GetMapping("/teams/{id}/matches")
    public ResponseEntity<List<Game>> getTeamMatches(@PathVariable int id) {
        List<Game> matches = teamService.getTeamMatches(id);
        return ResponseEntity.ok(matches);
    }

    // GET /teams/{id1}/head-to-head/{id2} - Get head-to-head matches
    @GetMapping("/teams/{id1}/head-to-head/{id2}")
    public ResponseEntity<List<Game>> getHeadToHead(@PathVariable int id1, @PathVariable int id2) {
        List<Game> matches = teamService.getHeadToHeadMatches(id1, id2);
        return ResponseEntity.ok(matches);
    }
}
