package com.example.demo.services.pandascore;

import com.example.demo.services.pandascore.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PandaScoreApiService {

    private static final Logger logger = LoggerFactory.getLogger(PandaScoreApiService.class);

    private final WebClient webClient;

    public PandaScoreApiService(
            @Value("${pandascore.api.base-url}") String baseUrl,
            @Value("${pandascore.api.token}") String apiToken) {

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiToken)
                .build();
    }

    /**
     * Fetch upcoming League of Legends matches
     * @param daysAhead How many days in the future to fetch matches (not used, kept for API compatibility)
     * @return List of upcoming matches
     */
    public List<PandaScoreMatch> getUpcomingMatches(int daysAhead) {
        logger.info("Fetching upcoming LoL matches");

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lol/matches/upcoming")
                            .queryParam("per_page", 100)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<PandaScoreMatch>>() {})
                    .onErrorResume(error -> {
                        logger.error("Error fetching upcoming matches: {}", error.getMessage());
                        return Mono.just(List.of());
                    })
                    .block();
        } catch (Exception e) {
            logger.error("Exception while fetching upcoming matches", e);
            return List.of();
        }
    }

    /**
     * Fetch running (live) League of Legends matches
     * @return List of currently running matches
     */
    public List<PandaScoreMatch> getRunningMatches() {
        logger.info("Fetching running LoL matches");

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lol/matches/running")
                            .queryParam("per_page", 50)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<PandaScoreMatch>>() {})
                    .onErrorResume(error -> {
                        logger.error("Error fetching running matches: {}", error.getMessage());
                        return Mono.just(List.of());
                    })
                    .block();
        } catch (Exception e) {
            logger.error("Exception while fetching running matches", e);
            return List.of();
        }
    }

    /**
     * Fetch past League of Legends matches
     * @param daysBack How many days in the past to fetch matches (not used, kept for API compatibility)
     * @return List of past matches
     */
    public List<PandaScoreMatch> getPastMatches(int daysBack) {
        logger.info("Fetching past LoL matches");

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lol/matches/past")
                            .queryParam("per_page", 100)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<PandaScoreMatch>>() {})
                    .onErrorResume(error -> {
                        logger.error("Error fetching past matches: {}", error.getMessage());
                        return Mono.just(List.of());
                    })
                    .block();
        } catch (Exception e) {
            logger.error("Exception while fetching past matches", e);
            return List.of();
        }
    }

    /**
     * Fetch a specific match by ID
     * @param matchId The PandaScore match ID
     * @return The match details
     */
    public PandaScoreMatch getMatchById(Long matchId) {
        logger.info("Fetching match with ID: {}", matchId);

        try {
            return webClient.get()
                    .uri("/lol/matches/{id}", matchId)
                    .retrieve()
                    .bodyToMono(PandaScoreMatch.class)
                    .onErrorResume(error -> {
                        logger.error("Error fetching match {}: {}", matchId, error.getMessage());
                        return Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            logger.error("Exception while fetching match {}", matchId, e);
            return null;
        }
    }

    /**
     * Fetch team details with players by team ID
     * @param teamId The PandaScore team ID
     * @return Team details including roster
     */
    public PandaScoreTeam getTeamById(Long teamId) {
        logger.info("Fetching team details for ID: {}", teamId);

        try {
            List<PandaScoreTeam> teams = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lol/teams")
                            .queryParam("filter[id]", teamId)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<PandaScoreTeam>>() {})
                    .onErrorResume(error -> {
                        logger.error("Error fetching team {}: {}", teamId, error.getMessage());
                        return Mono.just(List.of());
                    })
                    .block();

            if (teams != null && !teams.isEmpty()) {
                return teams.get(0);
            }
            return null;
        } catch (Exception e) {
            logger.error("Exception while fetching team {}", teamId, e);
            return null;
        }
    }

    /**
     * Fetch teams for a specific league/tournament
     * @param leagueName The league name to search for (e.g., "LCK", "LEC", "MSI")
     * @return List of teams in that league
     */
    public List<PandaScoreTeam> getTeamsByLeague(String leagueName) {
        logger.info("Fetching teams for league: {}", leagueName);

        try {
            // Fetch recent matches from the league to get active teams
            List<PandaScoreMatch> matches = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lol/matches")
                            .queryParam("per_page", 100)
                            .queryParam("filter[league_name]", leagueName)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<PandaScoreMatch>>() {})
                    .onErrorResume(error -> {
                        logger.error("Error fetching matches for league {}: {}", leagueName, error.getMessage());
                        return Mono.just(List.of());
                    })
                    .block();

            if (matches == null || matches.isEmpty()) {
                logger.warn("No matches found for league: {}", leagueName);
                return List.of();
            }

            // Extract unique teams from matches
            return matches.stream()
                    .flatMap(match -> match.getOpponents() != null ? match.getOpponents().stream() : java.util.stream.Stream.empty())
                    .map(PandaScoreOpponent::getOpponent)
                    .filter(team -> team != null && team.getId() != null)
                    .distinct()
                    .toList();

        } catch (Exception e) {
            logger.error("Exception while fetching teams for league {}", leagueName, e);
            return List.of();
        }
    }

    /**
     * Fetch betting odds for a specific match
     * NOTE: This requires a premium PandaScore subscription (Odds API access)
     * @param matchId The PandaScore match ID
     * @return List of odds for different markets, or empty list if not available
     */
    public List<PandaScoreOdds> getMatchOdds(Long matchId) {
        logger.info("Fetching odds for match: {}", matchId);

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lol/matches/{id}/odds")
                            .build(matchId))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<PandaScoreOdds>>() {})
                    .onErrorResume(error -> {
                        logger.debug("Odds not available for match {} (may require premium subscription): {}",
                                matchId, error.getMessage());
                        return Mono.just(List.of());
                    })
                    .block();
        } catch (Exception e) {
            logger.debug("Exception while fetching odds for match {} (odds API may not be enabled)", matchId);
            return List.of();
        }
    }
}
