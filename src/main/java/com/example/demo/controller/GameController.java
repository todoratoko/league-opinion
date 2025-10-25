package com.example.demo.controller;

import com.example.demo.model.entities.Game;
import com.example.demo.services.GameService;
import com.example.demo.services.pandascore.PandaScoreSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GameController {
    @Autowired
    GameService gameService;

    @Autowired
    PandaScoreSyncService pandaScoreSyncService;

    @Autowired
    com.example.demo.services.OddsScraperService oddsScraperService;

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

    /**
     * Manually trigger sync of past matches to fetch results
     * @param days Optional number of days to sync (default: 7)
     * @return Response with number of matches synced
     */
    @PostMapping("/games/sync/past")
    public ResponseEntity<Map<String, Object>> syncPastMatches(@RequestParam(defaultValue = "7") int days) {
        int synced = pandaScoreSyncService.syncPastMatches(days);
        return ResponseEntity.ok(Map.of(
            "message", "Past matches sync completed",
            "matchesSynced", synced,
            "daysScanned", days
        ));
    }

    /**
     * Manually trigger scraping of match results from OddsPortal
     * @return Response with number of results updated
     */
    @PostMapping("/games/scrape/results")
    public ResponseEntity<Map<String, Object>> scrapeResults() {
        int updated = oddsScraperService.scrapeAndUpdateResults();
        return ResponseEntity.ok(Map.of(
            "message", "Results scraping completed",
            "matchesUpdated", updated
        ));
    }
}
