package com.example.demo.controller;

import com.example.demo.services.pandascore.PandaScoreApiService;
import com.example.demo.services.pandascore.PandaScoreSyncService;
import com.example.demo.services.pandascore.dto.PandaScoreMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pandascore")
public class PandaScoreController {

    @Autowired
    private PandaScoreSyncService syncService;

    @Autowired
    private PandaScoreApiService apiService;

    /**
     * Manually trigger sync of upcoming matches
     */
    @PostMapping("/sync/upcoming")
    public ResponseEntity<Map<String, Object>> syncUpcomingMatches(
            @RequestParam(defaultValue = "14") int days) {

        Map<String, Object> response = new HashMap<>();

        try {
            int synced = syncService.syncUpcomingMatches(days);
            response.put("success", true);
            response.put("message", "Sync completed successfully");
            response.put("matchesSynced", synced);
            response.put("daysAhead", days);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Manually trigger sync of past matches
     */
    @PostMapping("/sync/past")
    public ResponseEntity<Map<String, Object>> syncPastMatches(
            @RequestParam(defaultValue = "7") int days) {

        Map<String, Object> response = new HashMap<>();

        try {
            int synced = syncService.syncPastMatches(days);
            response.put("success", true);
            response.put("message", "Sync completed successfully");
            response.put("matchesSynced", synced);
            response.put("daysBack", days);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Sync all teams for a specific league
     */
    @PostMapping("/sync/teams/{leagueName}")
    public ResponseEntity<Map<String, Object>> syncTeamsByLeague(
            @PathVariable String leagueName) {

        Map<String, Object> response = new HashMap<>();

        try {
            int synced = syncService.syncTeamsByLeague(leagueName);
            response.put("success", true);
            response.put("message", "Team sync completed successfully");
            response.put("teamsSynced", synced);
            response.put("league", leagueName);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Fetch upcoming matches from PandaScore (without saving to DB)
     * Useful for testing the API connection
     */
    @GetMapping("/test/upcoming")
    public ResponseEntity<Map<String, Object>> testFetchUpcoming(
            @RequestParam(defaultValue = "7") int days) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<PandaScoreMatch> matches = apiService.getUpcomingMatches(days);

            response.put("success", true);
            response.put("matchesFound", matches != null ? matches.size() : 0);
            response.put("matches", matches);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Fetch running matches from PandaScore
     */
    @GetMapping("/test/running")
    public ResponseEntity<Map<String, Object>> testFetchRunning() {

        Map<String, Object> response = new HashMap<>();

        try {
            List<PandaScoreMatch> matches = apiService.getRunningMatches();

            response.put("success", true);
            response.put("matchesFound", matches != null ? matches.size() : 0);
            response.put("matches", matches);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get a specific match by ID from PandaScore
     */
    @GetMapping("/test/match/{id}")
    public ResponseEntity<Map<String, Object>> testFetchMatchById(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            PandaScoreMatch match = apiService.getMatchById(id);

            response.put("success", match != null);
            response.put("match", match);

            if (match == null) {
                response.put("message", "Match not found");
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
