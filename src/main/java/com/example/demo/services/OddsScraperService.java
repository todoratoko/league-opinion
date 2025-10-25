package com.example.demo.services;

import com.example.demo.model.entities.Game;
import com.example.demo.model.entities.Team;
import com.example.demo.model.repositories.GameRepository;
import com.example.demo.model.repositories.TeamRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OddsScraperService {

    private static final Logger logger = LoggerFactory.getLogger(OddsScraperService.class);

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TeamRepository teamRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Execute the Python scraper and return the results
     */
    public List<ScrapedMatch> scrapeOdds() {
        List<ScrapedMatch> scrapedMatches = new ArrayList<>();

        try {
            // Get project root directory
            String projectRoot = System.getProperty("user.dir");
            String scriptPath = projectRoot + "/scripts/scrape_oddsportal.py";

            File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                logger.error("Python scraper script not found at: {}", scriptPath);
                return scrapedMatches;
            }

            // Execute Python script using venv
            String venvPython = projectRoot + "/scripts/venv/bin/python3";
            File venvPythonFile = new File(venvPython);

            // Use venv python if it exists, otherwise fall back to system python3
            String pythonCmd = venvPythonFile.exists() ? venvPython : "python3";

            ProcessBuilder processBuilder = new ProcessBuilder(pythonCmd, scriptPath);
            processBuilder.directory(new File(projectRoot));
            processBuilder.redirectErrorStream(false);

            logger.info("Executing odds scraper: {}", scriptPath);
            Process process = processBuilder.start();

            // Read stdout (JSON output)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // Read stderr (logs)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                logger.debug("Scraper log: {}", line);
            }

            int exitCode = process.waitFor();
            logger.info("Scraper exit code: {}", exitCode);

            if (exitCode == 0 && output.length() > 0) {
                // Parse JSON response
                JsonNode result = objectMapper.readTree(output.toString());

                if (result.has("success") && result.get("success").asBoolean()) {
                    JsonNode matches = result.get("matches");
                    if (matches != null && matches.isArray()) {
                        for (JsonNode matchNode : matches) {
                            ScrapedMatch match = new ScrapedMatch();
                            match.setTeam1(matchNode.get("team1").asText());
                            match.setTeam2(matchNode.get("team2").asText());
                            match.setTeam1Odds(matchNode.get("team1Odds").asDouble());
                            match.setTeam2Odds(matchNode.get("team2Odds").asDouble());
                            match.setScrapedAt(matchNode.get("scrapedAt").asText());
                            scrapedMatches.add(match);
                        }
                        logger.info("Successfully scraped {} matches", scrapedMatches.size());
                    }
                } else {
                    logger.error("Scraper returned unsuccessful result: {}", result);
                }
            } else {
                logger.error("Scraper execution failed with exit code: {}", exitCode);
            }

        } catch (Exception e) {
            logger.error("Error executing odds scraper: {}", e.getMessage(), e);
        }

        return scrapedMatches;
    }

    /**
     * Update database with scraped odds
     */
    @Transactional
    public int updateOddsInDatabase(List<ScrapedMatch> scrapedMatches) {
        int updated = 0;

        for (ScrapedMatch scrapedMatch : scrapedMatches) {
            try {
                // Find matching game by team names
                List<Team> team1Options = findTeamByName(scrapedMatch.getTeam1());
                List<Team> team2Options = findTeamByName(scrapedMatch.getTeam2());

                if (!team1Options.isEmpty() && !team2Options.isEmpty()) {
                    // Try to find game with these teams
                    for (Team team1 : team1Options) {
                        for (Team team2 : team2Options) {
                            List<Game> games = gameRepository.findUpcomingGamesByTeams(
                                    (int) team1.getId(),
                                    (int) team2.getId()
                            );

                            for (Game game : games) {
                                if (!game.getIsFinished()) {
                                    // CRITICAL: Match the scraped teams to the correct game teams
                                    // team1 from scraper might be teamOne or teamTwo in the game
                                    boolean team1IsTeamOne = (game.getTeamOneId() == team1.getId());

                                    if (team1IsTeamOne) {
                                        // Scraped team1 matches game's teamOne
                                        game.setTeamOneOdds(scrapedMatch.getTeam1Odds());
                                        game.setTeamTwoOdds(scrapedMatch.getTeam2Odds());
                                    } else {
                                        // Scraped team1 matches game's teamTwo (swapped)
                                        game.setTeamOneOdds(scrapedMatch.getTeam2Odds());
                                        game.setTeamTwoOdds(scrapedMatch.getTeam1Odds());
                                    }

                                    game.setOddsLastUpdated(scrapedMatch.getScrapedAt());
                                    gameRepository.save(game);
                                    updated++;
                                    logger.info("Updated odds for match: {} vs {} - Game[{} vs {}] Odds[{}/{}]",
                                            scrapedMatch.getTeam1(), scrapedMatch.getTeam2(),
                                            team1.getName(), team2.getName(),
                                            game.getTeamOneOdds(), game.getTeamTwoOdds());
                                }
                            }
                        }
                    }
                } else {
                    logger.debug("Could not find teams in database: {} vs {}",
                            scrapedMatch.getTeam1(), scrapedMatch.getTeam2());
                }

            } catch (Exception e) {
                logger.error("Error updating odds for match {} vs {}: {}",
                        scrapedMatch.getTeam1(), scrapedMatch.getTeam2(), e.getMessage());
            }
        }

        return updated;
    }

    /**
     * Find team by name (tries various matching strategies with fuzzy matching)
     * Returns the best match when multiple candidates exist
     */
    private List<Team> findTeamByName(String teamName) {
        List<Team> results = new ArrayList<>();

        // Normalize the search term
        String normalizedSearch = normalizeTeamName(teamName);

        // Exact match by name
        List<Team> exactMatches = teamRepository.findByName(teamName);
        if (!exactMatches.isEmpty()) {
            if (exactMatches.size() == 1) {
                results.add(exactMatches.get(0));
                return results;
            }
            // Multiple exact matches - will rank them below with other candidates
        }

        // Try by acronym/tag
        List<Team> tagMatches = teamRepository.findByTag(teamName);
        if (!tagMatches.isEmpty()) {
            if (tagMatches.size() == 1 && exactMatches.isEmpty()) {
                results.add(tagMatches.get(0));
                return results;
            }
            // Multiple tag matches - will rank them below with other candidates
        }

        // If we have multiple exact/tag matches, add them to ranking with high scores
        List<Team> allTeams = teamRepository.findAll();
        List<TeamMatch> matches = new ArrayList<>();

        for (Team team : allTeams) {
            int score = 0;

            // Check if this team is in exact matches (highest priority)
            if (exactMatches.contains(team)) {
                score = 1100;  // Highest score for exact name match
            }
            // Check if this team is in tag matches (second highest priority)
            else if (tagMatches.contains(team)) {
                score = 1050;  // High score for tag match
            }
            // Exact full name match
            else if (team.getFullName() != null && team.getFullName().equalsIgnoreCase(teamName)) {
                score = 1000;
            } else {
                // Fuzzy matching with scoring
                String normalizedDbName = normalizeTeamName(team.getName());
                String normalizedDbFullName = team.getFullName() != null ? normalizeTeamName(team.getFullName()) : "";

                // Exact match on normalized name
                if (normalizedSearch.equals(normalizedDbName)) {
                    score = 900;
                } else if (normalizedDbFullName.equals(normalizedSearch)) {
                    score = 850;
                }
                // Contains matching (bidirectional)
                else if (normalizedDbName.contains(normalizedSearch)) {
                    score = 500;  // DB name contains search term
                } else if (normalizedSearch.contains(normalizedDbName)) {
                    score = 400;  // Search term contains DB name
                } else if (!normalizedDbFullName.isEmpty() && normalizedDbFullName.contains(normalizedSearch)) {
                    score = 300;
                } else if (!normalizedDbFullName.isEmpty() && normalizedSearch.contains(normalizedDbFullName)) {
                    score = 200;
                }
            }

            // Bonus points for having PandaScore ID (indicates more complete data)
            if (score > 0 && team.getPandascoreTeamId() != null) {
                score += 50;
            }

            if (score > 0) {
                matches.add(new TeamMatch(team, score));
            }
        }

        // Sort by score (highest first) and return only the best match
        if (!matches.isEmpty()) {
            matches.sort((a, b) -> Integer.compare(b.score, a.score));

            TeamMatch bestMatch = matches.get(0);

            // Log if there are multiple matches
            if (matches.size() > 1) {
                logger.debug("Multiple matches for '{}': selecting {} (score: {}) over {} other candidates",
                        teamName, bestMatch.team.getName(), bestMatch.score, matches.size() - 1);
            }

            results.add(bestMatch.team);
        }

        return results;
    }

    /**
     * Helper class for ranking team matches
     */
    private static class TeamMatch {
        Team team;
        int score;

        TeamMatch(Team team, int score) {
            this.team = team;
            this.score = score;
        }
    }

    /**
     * Normalize team name for fuzzy matching
     * Removes common suffixes, converts to lowercase, removes extra spaces
     */
    private String normalizeTeamName(String name) {
        if (name == null) return "";

        String normalized = name.toLowerCase().trim();

        // Remove common esports suffixes
        normalized = normalized.replaceAll("\\s+(esports?|gaming|gg|academy)\\s*$", "");

        // Remove extra whitespace
        normalized = normalized.replaceAll("\\s+", " ");

        return normalized;
    }

    /**
     * Run full odds scraping and update cycle
     */
    public int scrapeAndUpdateOdds() {
        logger.info("Starting odds scrape and update cycle");

        List<ScrapedMatch> scrapedMatches = scrapeOdds();

        if (scrapedMatches.isEmpty()) {
            logger.warn("No matches scraped, skipping database update");
            return 0;
        }

        int updated = updateOddsInDatabase(scrapedMatches);
        logger.info("Odds update complete: {} matches updated", updated);

        return updated;
    }

    /**
     * Execute the Python results scraper and return finished matches with scores
     */
    public List<ScrapedResult> scrapeResults() {
        List<ScrapedResult> scrapedResults = new ArrayList<>();

        try {
            // Get project root directory
            String projectRoot = System.getProperty("user.dir");
            String scriptPath = projectRoot + "/scripts/scrape_liquipedia.py";

            File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                logger.error("Python results scraper script not found at: {}", scriptPath);
                return scrapedResults;
            }

            // Execute Python script using venv
            String venvPython = projectRoot + "/scripts/venv/bin/python3";
            File venvPythonFile = new File(venvPython);

            // Use venv python if it exists, otherwise fall back to system python3
            String pythonCmd = venvPythonFile.exists() ? venvPython : "python3";

            ProcessBuilder processBuilder = new ProcessBuilder(pythonCmd, scriptPath);
            processBuilder.directory(new File(projectRoot));
            processBuilder.redirectErrorStream(false);

            logger.info("Executing results scraper: {}", scriptPath);
            Process process = processBuilder.start();

            // Read stdout (JSON output)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // Read stderr (logs)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                logger.debug("Results scraper log: {}", line);
            }

            int exitCode = process.waitFor();
            logger.info("Results scraper exit code: {}", exitCode);

            if (exitCode == 0 && output.length() > 0) {
                // Parse JSON response
                JsonNode result = objectMapper.readTree(output.toString());

                if (result.has("success") && result.get("success").asBoolean()) {
                    JsonNode results = result.get("results");
                    if (results != null && results.isArray()) {
                        for (JsonNode resultNode : results) {
                            ScrapedResult scrapedResult = new ScrapedResult();
                            scrapedResult.setTeam1(resultNode.get("team1").asText());
                            scrapedResult.setTeam2(resultNode.get("team2").asText());
                            scrapedResult.setTeam1Score(resultNode.get("team1Score").asInt());
                            scrapedResult.setTeam2Score(resultNode.get("team2Score").asInt());
                            scrapedResult.setScrapedAt(resultNode.get("scrapedAt").asText());

                            // Extract matchDateTime if available
                            if (resultNode.has("matchDateTime")) {
                                scrapedResult.setMatchDateTime(resultNode.get("matchDateTime").asText());
                            }

                            scrapedResults.add(scrapedResult);
                        }
                        logger.info("Successfully scraped {} match results", scrapedResults.size());
                    }
                } else {
                    logger.error("Results scraper returned unsuccessful result: {}", result);
                }
            } else {
                logger.error("Results scraper execution failed with exit code: {}", exitCode);
            }

        } catch (Exception e) {
            logger.error("Error executing results scraper: {}", e.getMessage(), e);
        }

        return scrapedResults;
    }

    /**
     * Update database with scraped match results
     */
    @Transactional
    public int updateResultsInDatabase(List<ScrapedResult> scrapedResults) {
        int updated = 0;

        for (ScrapedResult scrapedResult : scrapedResults) {
            try {
                // Find matching teams by name
                List<Team> team1Options = findTeamByName(scrapedResult.getTeam1());
                List<Team> team2Options = findTeamByName(scrapedResult.getTeam2());

                if (!team1Options.isEmpty() && !team2Options.isEmpty()) {
                    Team team1 = team1Options.get(0);
                    Team team2 = team2Options.get(0);

                    // Try to find existing games between these teams
                    List<Game> games = gameRepository.findHeadToHeadMatches(
                            (int) team1.getId(),
                            (int) team2.getId()
                    );

                    boolean gameUpdated = false;

                    // Try to update existing game
                    for (Game game : games) {
                        // Update if not already finished with scores, or if scores are missing
                        if (game.getTeamOneScore() == null || game.getTeamTwoScore() == null) {
                            // Match the scraped teams to the correct game teams
                            boolean team1IsTeamOne = (game.getTeamOneId() == team1.getId());

                            if (team1IsTeamOne) {
                                // Scraped team1 matches game's teamOne
                                game.setTeamOneScore(scrapedResult.getTeam1Score());
                                game.setTeamTwoScore(scrapedResult.getTeam2Score());
                            } else {
                                // Scraped team1 matches game's teamTwo (swapped)
                                game.setTeamOneScore(scrapedResult.getTeam2Score());
                                game.setTeamTwoScore(scrapedResult.getTeam1Score());
                            }

                            // Mark as finished
                            game.setIsFinished(true);
                            game.setMatchStatus("finished");

                            // Set match date/time if available
                            if (scrapedResult.getMatchDateTime() != null && !scrapedResult.getMatchDateTime().isEmpty()) {
                                game.setMatchStartDateTime(scrapedResult.getMatchDateTime());
                                logger.debug("Updated match date to: {}", scrapedResult.getMatchDateTime());
                            }

                            gameRepository.save(game);
                            updated++;
                            gameUpdated = true;
                            logger.info("Updated result for match: {} vs {} - Game[{} {} - {} {}]",
                                    scrapedResult.getTeam1(), scrapedResult.getTeam2(),
                                    team1.getName(), game.getTeamOneScore(),
                                    game.getTeamTwoScore(), team2.getName());
                            break; // Only update the first matching game
                        }
                    }

                    // If no existing game found, create a new one
                    if (!gameUpdated) {
                        Game newGame = new Game();
                        newGame.setTeamOneId((int) team1.getId());
                        newGame.setTeamTwoId((int) team2.getId());
                        newGame.setTeamOneScore(scrapedResult.getTeam1Score());
                        newGame.setTeamTwoScore(scrapedResult.getTeam2Score());
                        newGame.setIsFinished(true);
                        newGame.setMatchStatus("finished");

                        // Set match date/time if available
                        if (scrapedResult.getMatchDateTime() != null && !scrapedResult.getMatchDateTime().isEmpty()) {
                            newGame.setMatchStartDateTime(scrapedResult.getMatchDateTime());
                            logger.debug("Set match date to: {}", scrapedResult.getMatchDateTime());
                        }

                        gameRepository.save(newGame);
                        updated++;
                        logger.info("Created new finished match: {} vs {} ({}-{}) at {}",
                                team1.getName(), team2.getName(),
                                scrapedResult.getTeam1Score(), scrapedResult.getTeam2Score(),
                                newGame.getMatchStartDateTime());
                    }
                } else {
                    logger.debug("Could not find teams in database for result: {} vs {}",
                            scrapedResult.getTeam1(), scrapedResult.getTeam2());
                }

            } catch (Exception e) {
                logger.error("Error updating result for match {} vs {}: {}",
                        scrapedResult.getTeam1(), scrapedResult.getTeam2(), e.getMessage());
            }
        }

        return updated;
    }

    /**
     * Run full results scraping and update cycle
     */
    public int scrapeAndUpdateResults() {
        logger.info("Starting results scrape and update cycle");

        List<ScrapedResult> scrapedResults = scrapeResults();

        if (scrapedResults.isEmpty()) {
            logger.warn("No results scraped, skipping database update");
            return 0;
        }

        int updated = updateResultsInDatabase(scrapedResults);
        logger.info("Results update complete: {} matches updated", updated);

        return updated;
    }

    /**
     * Inner class for scraped match data
     */
    public static class ScrapedMatch {
        private String team1;
        private String team2;
        private Double team1Odds;
        private Double team2Odds;
        private String scrapedAt;

        // Getters and setters
        public String getTeam1() { return team1; }
        public void setTeam1(String team1) { this.team1 = team1; }

        public String getTeam2() { return team2; }
        public void setTeam2(String team2) { this.team2 = team2; }

        public Double getTeam1Odds() { return team1Odds; }
        public void setTeam1Odds(Double team1Odds) { this.team1Odds = team1Odds; }

        public Double getTeam2Odds() { return team2Odds; }
        public void setTeam2Odds(Double team2Odds) { this.team2Odds = team2Odds; }

        public String getScrapedAt() { return scrapedAt; }
        public void setScrapedAt(String scrapedAt) { this.scrapedAt = scrapedAt; }
    }

    /**
     * Inner class for scraped match results
     */
    public static class ScrapedResult {
        private String team1;
        private String team2;
        private Integer team1Score;
        private Integer team2Score;
        private String scrapedAt;
        private String matchDateTime;

        // Getters and setters
        public String getTeam1() { return team1; }
        public void setTeam1(String team1) { this.team1 = team1; }

        public String getTeam2() { return team2; }
        public void setTeam2(String team2) { this.team2 = team2; }

        public Integer getTeam1Score() { return team1Score; }
        public void setTeam1Score(Integer team1Score) { this.team1Score = team1Score; }

        public Integer getTeam2Score() { return team2Score; }
        public void setTeam2Score(Integer team2Score) { this.team2Score = team2Score; }

        public String getScrapedAt() { return scrapedAt; }
        public void setScrapedAt(String scrapedAt) { this.scrapedAt = scrapedAt; }

        public String getMatchDateTime() { return matchDateTime; }
        public void setMatchDateTime(String matchDateTime) { this.matchDateTime = matchDateTime; }
    }
}
