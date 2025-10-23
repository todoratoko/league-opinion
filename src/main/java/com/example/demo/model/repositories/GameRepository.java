package com.example.demo.model.repositories;

import com.example.demo.model.entities.Game;
import com.example.demo.model.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByRegion(Region region);
    List<Game> findByRegionId(Long regionId);

    // Find all games where a team played (either as team1 or team2)
    @Query("SELECT g FROM Game g WHERE g.teamOneId = :teamId OR g.teamTwoId = :teamId ORDER BY g.matchStartDateTime DESC")
    List<Game> findByTeamId(@Param("teamId") int teamId);

    // Find head-to-head matches between two teams
    @Query("SELECT g FROM Game g WHERE (g.teamOneId = :team1Id AND g.teamTwoId = :team2Id) OR (g.teamOneId = :team2Id AND g.teamTwoId = :team1Id) ORDER BY g.matchStartDateTime DESC")
    List<Game> findHeadToHeadMatches(@Param("team1Id") int team1Id, @Param("team2Id") int team2Id);

    // Find finished games for a team
    @Query("SELECT g FROM Game g WHERE (g.teamOneId = :teamId OR g.teamTwoId = :teamId) AND g.isFinished = true ORDER BY g.matchStartDateTime DESC")
    List<Game> findFinishedGamesByTeamId(@Param("teamId") int teamId);
}
