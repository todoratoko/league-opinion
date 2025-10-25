package com.example.demo.services.pandascore;

import com.example.demo.model.entities.Game;
import com.example.demo.model.entities.Player;
import com.example.demo.model.entities.Region;
import com.example.demo.model.entities.Team;
import com.example.demo.model.repositories.GameRepository;
import com.example.demo.model.repositories.PlayerRepository;
import com.example.demo.model.repositories.RegionRepository;
import com.example.demo.model.repositories.TeamRepository;
import com.example.demo.services.pandascore.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class PandaScoreSyncService {

    private static final Logger logger = LoggerFactory.getLogger(PandaScoreSyncService.class);

    @Autowired
    private PandaScoreApiService pandaScoreApiService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Value("${pandascore.api.allowed-leagues}")
    private String allowedLeaguesStr;

    /**
     * Sync upcoming matches from PandaScore to the database
     * @param daysAhead How many days ahead to fetch
     * @return Number of matches synced
     */
    @Transactional
    public int syncUpcomingMatches(int daysAhead) {
        logger.info("Starting sync of upcoming matches for next {} days", daysAhead);

        List<PandaScoreMatch> matches = pandaScoreApiService.getUpcomingMatches(daysAhead);

        if (matches == null || matches.isEmpty()) {
            logger.warn("No upcoming matches found from PandaScore");
            return 0;
        }

        int synced = 0;
        for (PandaScoreMatch match : matches) {
            try {
                if (syncMatch(match)) {
                    synced++;
                }
            } catch (Exception e) {
                logger.error("Error syncing match {}: {}", match.getId(), e.getMessage(), e);
            }
        }

        logger.info("Successfully synced {} out of {} matches", synced, matches.size());
        return synced;
    }

    /**
     * Sync past matches from PandaScore to the database
     * @param daysBack How many days back to fetch (not used by API, kept for compatibility)
     * @return Number of matches synced
     */
    @Transactional
    public int syncPastMatches(int daysBack) {
        logger.info("Starting sync of past matches");

        List<PandaScoreMatch> matches = pandaScoreApiService.getPastMatches(daysBack);

        if (matches == null || matches.isEmpty()) {
            logger.warn("No past matches found from PandaScore");
            return 0;
        }

        int synced = 0;
        for (PandaScoreMatch match : matches) {
            try {
                if (syncMatch(match)) {
                    synced++;
                }
            } catch (Exception e) {
                logger.error("Error syncing match {}: {}", match.getId(), e.getMessage(), e);
            }
        }

        logger.info("Successfully synced {} out of {} past matches", synced, matches.size());
        return synced;
    }

    /**
     * Sync all teams for a specific league
     * @param leagueName The league name (e.g., "LCK", "LEC", "MSI")
     * @return Number of teams synced
     */
    @Transactional
    public int syncTeamsByLeague(String leagueName) {
        logger.info("Starting sync of teams for league: {}", leagueName);

        List<PandaScoreTeam> teams = pandaScoreApiService.getTeamsByLeague(leagueName);

        if (teams == null || teams.isEmpty()) {
            logger.warn("No teams found for league: {}", leagueName);
            return 0;
        }

        int synced = 0;
        for (PandaScoreTeam pandaTeam : teams) {
            try {
                Team team = syncTeam(pandaTeam);
                if (team != null) {
                    synced++;
                }
            } catch (Exception e) {
                logger.error("Error syncing team {}: {}", pandaTeam.getName(), e.getMessage(), e);
            }
        }

        logger.info("Successfully synced {} out of {} teams for league {}", synced, teams.size(), leagueName);
        return synced;
    }

    /**
     * Sync a single match from PandaScore
     * @param pandaMatch The PandaScore match data
     * @return true if synced successfully
     */
    @Transactional
    public boolean syncMatch(PandaScoreMatch pandaMatch) {
        if (pandaMatch == null || pandaMatch.getId() == null) {
            logger.warn("Invalid match data, skipping");
            return false;
        }

        // Check if league is allowed
        if (!isLeagueAllowed(pandaMatch)) {
            logger.debug("Match {} from league {} is not in allowed leagues, skipping",
                    pandaMatch.getId(),
                    pandaMatch.getLeague() != null ? pandaMatch.getLeague().getName() : "unknown");
            return false;
        }

        // Check if we need both opponents
        if (pandaMatch.getOpponents() == null || pandaMatch.getOpponents().size() < 2) {
            logger.warn("Match {} has less than 2 opponents, skipping", pandaMatch.getId());
            return false;
        }

        // Check if match already exists
        Game existingGame = gameRepository.findByPandascoreMatchId(pandaMatch.getId());

        if (existingGame != null) {
            // Update existing match
            return updateExistingMatch(existingGame, pandaMatch);
        } else {
            // Create new match
            return createNewMatch(pandaMatch);
        }
    }

    /**
     * Check if a match's league is in the allowed leagues list
     */
    private boolean isLeagueAllowed(PandaScoreMatch match) {
        if (match.getLeague() == null || match.getLeague().getName() == null) {
            return false;
        }

        List<String> allowedLeagues = Arrays.asList(allowedLeaguesStr.split(","));
        String leagueName = match.getLeague().getName();

        return allowedLeagues.stream()
                .anyMatch(allowed -> leagueName.toLowerCase().contains(allowed.toLowerCase().trim()));
    }

    private boolean createNewMatch(PandaScoreMatch pandaMatch) {
        try {
            // Get or create teams
            PandaScoreOpponent opponent1 = pandaMatch.getOpponents().get(0);
            PandaScoreOpponent opponent2 = pandaMatch.getOpponents().get(1);

            if (opponent1.getOpponent() == null || opponent2.getOpponent() == null) {
                logger.warn("Match {} has null opponents, skipping", pandaMatch.getId());
                return false;
            }

            Team team1 = syncTeam(opponent1.getOpponent());
            Team team2 = syncTeam(opponent2.getOpponent());

            if (team1 == null || team2 == null) {
                logger.warn("Could not sync teams for match {}, skipping", pandaMatch.getId());
                return false;
            }

            // Create new game
            Game game = new Game();
            game.setPandascoreMatchId(pandaMatch.getId());
            game.setTeamOneId((int) team1.getId());
            game.setTeamTwoId((int) team2.getId());

            // Set match date
            if (pandaMatch.getScheduledAt() != null) {
                game.setMatchStartDateTime(pandaMatch.getScheduledAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            } else if (pandaMatch.getBeginAt() != null) {
                game.setMatchStartDateTime(pandaMatch.getBeginAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }

            // Set match status
            game.setMatchStatus(pandaMatch.getStatus());
            game.setIsFinished("finished".equalsIgnoreCase(pandaMatch.getStatus()));

            // Set match type (Bo1, Bo3, Bo5)
            if (pandaMatch.getNumberOfGames() != null) {
                game.setMatchType("Bo" + pandaMatch.getNumberOfGames());
            }

            // Set tournament info
            if (pandaMatch.getTournament() != null) {
                game.setTournament(pandaMatch.getTournament().getName());
            }

            // Set league info
            if (pandaMatch.getLeague() != null) {
                String leagueName = pandaMatch.getLeague().getName();
                game.setLeagueName(leagueName);
                game.setLeagueCode(mapLeagueNameToCode(leagueName));
            }

            // Set serie info
            if (pandaMatch.getSerie() != null) {
                game.setSerieName(pandaMatch.getSerie().getFullName());
            }

            // Set region based on league
            Region region = determineRegion(pandaMatch);
            game.setRegion(region);

            // Initialize percentages to 50-50
            game.setTeamOnePercent(50);
            game.setTeamTwoPercent(50);

            // Save game
            gameRepository.save(game);
            logger.info("Created new match: {} vs {} (PandaScore ID: {})",
                    team1.getName(), team2.getName(), pandaMatch.getId());

            // Try to fetch and store odds (will silently fail if not available)
            syncOddsForMatch(game, pandaMatch.getId());

            return true;
        } catch (Exception e) {
            logger.error("Error creating match {}: {}", pandaMatch.getId(), e.getMessage(), e);
            return false;
        }
    }

    private boolean updateExistingMatch(Game game, PandaScoreMatch pandaMatch) {
        try {
            // Update status
            game.setMatchStatus(pandaMatch.getStatus());
            game.setIsFinished("finished".equalsIgnoreCase(pandaMatch.getStatus()));

            // Update scores if match is finished
            if (pandaMatch.getResults() != null && game.getIsFinished()) {
                // Note: PandaScore returns results as a list, we need to map them to team scores
                // This is a simplified version - you might need to enhance this
                game.setTeamOneScore(0);
                game.setTeamTwoScore(0);
            }

            gameRepository.save(game);
            logger.debug("Updated match {} (PandaScore ID: {})", game.getId(), pandaMatch.getId());

            return true;
        } catch (Exception e) {
            logger.error("Error updating match {}: {}", game.getId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Sync a team from PandaScore to database
     * @param pandaTeam The PandaScore team data
     * @return The Team entity (created or existing)
     */
    @Transactional
    public Team syncTeam(PandaScoreTeam pandaTeam) {
        if (pandaTeam == null || pandaTeam.getId() == null) {
            return null;
        }

        // Check if team already exists
        Team existingTeam = teamRepository.findByPandascoreTeamId(pandaTeam.getId());

        if (existingTeam != null) {
            // Update team info if needed
            updateTeamInfo(existingTeam, pandaTeam);
            // Sync players for existing team
            syncPlayersForTeam(existingTeam, pandaTeam);
            return existingTeam;
        }

        // Fetch full team details with players
        PandaScoreTeam fullTeamData = pandaScoreApiService.getTeamById(pandaTeam.getId());
        if (fullTeamData == null) {
            fullTeamData = pandaTeam;  // Fallback to original data
        }

        // Create new team
        Team team = new Team();
        team.setPandascoreTeamId(fullTeamData.getId());
        team.setName(fullTeamData.getAcronym() != null ? fullTeamData.getAcronym() : fullTeamData.getName());
        team.setFullName(fullTeamData.getName());  // Full team name
        team.setTag(fullTeamData.getAcronym());
        team.setImage(fullTeamData.getImageUrl());
        team.setRegion(fullTeamData.getLocation());
        team.setCountry(fullTeamData.getLocation());

        team = teamRepository.save(team);
        logger.info("Created new team: {} (PandaScore ID: {})", team.getName(), fullTeamData.getId());

        // Sync players for new team
        syncPlayersForTeam(team, fullTeamData);

        return team;
    }

    private void updateTeamInfo(Team team, PandaScoreTeam pandaTeam) {
        boolean updated = false;

        // Update image if changed
        if (pandaTeam.getImageUrl() != null && !pandaTeam.getImageUrl().equals(team.getImage())) {
            team.setImage(pandaTeam.getImageUrl());
            updated = true;
        }

        // Update fullName if not set or changed
        if (pandaTeam.getName() != null && !pandaTeam.getName().equals(team.getFullName())) {
            team.setFullName(pandaTeam.getName());
            updated = true;
        }

        // Update tag if changed
        if (pandaTeam.getAcronym() != null && !pandaTeam.getAcronym().equals(team.getTag())) {
            team.setTag(pandaTeam.getAcronym());
            if (team.getName() == null || team.getName().isEmpty()) {
                team.setName(pandaTeam.getAcronym());
            }
            updated = true;
        }

        if (updated) {
            teamRepository.save(team);
            logger.debug("Updated team info for: {}", team.getName());
        }
    }

    /**
     * Determine region based on league name
     */
    private Region determineRegion(PandaScoreMatch match) {
        if (match.getLeague() == null || match.getLeague().getName() == null) {
            return regionRepository.findById(5L).orElse(null); // International
        }

        String leagueName = match.getLeague().getName().toUpperCase();

        // Map leagues to regions
        if (leagueName.contains("LCK")) {
            return regionRepository.findById(3L).orElse(null); // Korea
        } else if (leagueName.contains("LPL")) {
            return regionRepository.findById(4L).orElse(null); // China
        } else if (leagueName.contains("LEC")) {
            return regionRepository.findById(2L).orElse(null); // Europe
        } else if (leagueName.contains("LCS") || leagueName.contains("LTA")) {
            return regionRepository.findById(1L).orElse(null); // North America
        } else {
            return regionRepository.findById(5L).orElse(null); // International (Worlds, MSI, etc.)
        }
    }

    /**
     * Sync players for a team from PandaScore data
     */
    @Transactional
    private void syncPlayersForTeam(Team team, PandaScoreTeam pandaTeam) {
        if (pandaTeam.getPlayers() == null || pandaTeam.getPlayers().isEmpty()) {
            logger.debug("No players found for team {}", team.getName());
            return;
        }

        int synced = 0;
        for (PandaScorePlayer pandaPlayer : pandaTeam.getPlayers()) {
            try {
                syncPlayer(pandaPlayer, team);
                synced++;
            } catch (Exception e) {
                logger.error("Error syncing player {} for team {}: {}",
                        pandaPlayer.getName(), team.getName(), e.getMessage());
            }
        }

        logger.info("Synced {} players for team {}", synced, team.getName());
    }

    /**
     * Sync an individual player
     */
    @Transactional
    private void syncPlayer(PandaScorePlayer pandaPlayer, Team team) {
        if (pandaPlayer == null || pandaPlayer.getId() == null) {
            return;
        }

        // Check if player already exists
        Player existingPlayer = playerRepository.findByPandascorePlayerId(pandaPlayer.getId());

        if (existingPlayer != null) {
            // Update existing player
            updatePlayerInfo(existingPlayer, pandaPlayer, team);
            return;
        }

        // Create new player
        Player player = new Player();
        player.setPandascorePlayerId(pandaPlayer.getId());
        player.setName(pandaPlayer.getName());
        player.setRole(mapRole(pandaPlayer.getRole()));
        player.setImage(pandaPlayer.getImageUrl());
        player.setFirstName(pandaPlayer.getFirstName());
        player.setLastName(pandaPlayer.getLastName());
        player.setNationality(pandaPlayer.getNationality());
        player.setTeam(team);

        playerRepository.save(player);
        logger.debug("Created player: {} for team {}", player.getName(), team.getName());
    }

    /**
     * Update existing player information
     */
    private void updatePlayerInfo(Player player, PandaScorePlayer pandaPlayer, Team team) {
        boolean updated = false;

        if (pandaPlayer.getImageUrl() != null && !pandaPlayer.getImageUrl().equals(player.getImage())) {
            player.setImage(pandaPlayer.getImageUrl());
            updated = true;
        }

        if (pandaPlayer.getName() != null && !pandaPlayer.getName().equals(player.getName())) {
            player.setName(pandaPlayer.getName());
            updated = true;
        }

        String mappedRole = mapRole(pandaPlayer.getRole());
        if (mappedRole != null && !mappedRole.equals(player.getRole())) {
            player.setRole(mappedRole);
            updated = true;
        }

        // Update team if changed
        if (team != null && (player.getTeam() == null || player.getTeam().getId() != team.getId())) {
            player.setTeam(team);
            updated = true;
        }

        if (updated) {
            playerRepository.save(player);
            logger.debug("Updated player: {}", player.getName());
        }
    }

    /**
     * Map PandaScore role names to our format
     */
    private String mapRole(String pandaRole) {
        if (pandaRole == null) {
            return null;
        }

        return switch (pandaRole.toLowerCase()) {
            case "top" -> "Top";
            case "jun", "jungle" -> "Jungle";
            case "mid", "middle" -> "Mid";
            case "adc", "bot" -> "ADC";
            case "sup", "support" -> "Support";
            default -> pandaRole;
        };
    }

    /**
     * Map league name to league code for frontend
     */
    private String mapLeagueNameToCode(String leagueName) {
        if (leagueName == null) {
            return null;
        }

        String lowerName = leagueName.toLowerCase();

        // Map league names to codes
        if (lowerName.contains("world") || lowerName.equals("worlds")) {
            return "WORLDS";
        } else if (lowerName.contains("msi") || lowerName.contains("mid-season")) {
            return "MSI";
        } else if (lowerName.contains("lck") || lowerName.contains("champions korea")) {
            return "LCK";
        } else if (lowerName.contains("lpl") || lowerName.contains("pro league")) {
            return "LPL";
        } else if (lowerName.contains("lec") || lowerName.contains("european championship")) {
            return "LEC";
        } else if (lowerName.contains("lcs") || lowerName.contains("lta") || lowerName.contains("the americas")) {
            return "LTA";
        } else if (lowerName.contains("lcp") || lowerName.contains("pacific")) {
            return "LCP";
        } else if (lowerName.contains("emea")) {
            return "EMEA";
        }

        // Default: try to extract acronym or return null
        return null;
    }

    /**
     * Sync betting odds for a match
     * Note: This will silently fail if odds API is not available (requires premium subscription)
     */
    private void syncOddsForMatch(Game game, Long pandaScoreMatchId) {
        try {
            List<PandaScoreOdds> oddsList = pandaScoreApiService.getMatchOdds(pandaScoreMatchId);

            if (oddsList == null || oddsList.isEmpty()) {
                return;  // No odds available, silently continue
            }

            // Find the "match_winner" market odds
            for (PandaScoreOdds odds : oddsList) {
                if ("match_winner".equalsIgnoreCase(odds.getMarketName()) && odds.getOptions() != null && odds.getOptions().size() >= 2) {
                    // Extract odds for both teams
                    PandaScoreOddsOption team1Odds = odds.getOptions().get(0);
                    PandaScoreOddsOption team2Odds = odds.getOptions().get(1);

                    game.setTeamOneOdds(team1Odds.getOdd());
                    game.setTeamTwoOdds(team2Odds.getOdd());
                    if (odds.getUpdatedAt() != null) {
                        game.setOddsLastUpdated(odds.getUpdatedAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    }

                    gameRepository.save(game);
                    logger.info("Updated odds for match {}: Team1={}, Team2={}",
                            game.getId(), game.getTeamOneOdds(), game.getTeamTwoOdds());
                    break;
                }
            }
        } catch (Exception e) {
            logger.debug("Could not fetch odds for match {} (likely not available with current subscription)", pandaScoreMatchId);
        }
    }
}
