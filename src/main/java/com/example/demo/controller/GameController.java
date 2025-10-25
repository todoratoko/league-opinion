package com.example.demo.controller;

import com.example.demo.model.entities.Game;
import com.example.demo.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameController {
    @Autowired
    GameService gameService;

    @GetMapping("/game/{id}")
    public ResponseEntity<Game> getById(@PathVariable int id) {
        Game game = gameService.getById(id);
        return ResponseEntity.ok(game);
    }

    @GetMapping("/games")
    public ResponseEntity<List<Game>> getGames(@RequestParam(required = false) Long regionId) {
        if (regionId != null) {
            List<Game> games = gameService.getGamesByRegionId(regionId);
            return ResponseEntity.ok(games);
        } else {
            List<Game> games = gameService.getAllGames();
            return ResponseEntity.ok(games);
        }
    }

    @GetMapping("/games/region/{regionId}")
    public ResponseEntity<List<Game>> getGamesByRegion(@PathVariable Long regionId) {
        List<Game> games = gameService.getGamesByRegionId(regionId);
        return ResponseEntity.ok(games);
    }

    /**
     * Get all currently running (live) games
     * @return List of games with status "running"
     */
    @GetMapping("/games/live")
    public ResponseEntity<List<Game>> getLiveGames() {
        List<Game> liveGames = gameService.getRunningGames();
        return ResponseEntity.ok(liveGames);
    }
}
