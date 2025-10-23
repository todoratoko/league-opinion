package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.Game;
import com.example.demo.model.entities.Team;
import com.example.demo.model.repositories.GameRepository;
import com.example.demo.model.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    GameRepository gameRepository;

    public Team getById(long id) {
        Optional<Team> team = teamRepository.findById(id);
        if(team.isPresent()){
            return team.get();
        }else{
            throw new NotFoundException("Team not found");
        }
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public List<Game> getTeamMatches(int teamId) {
        return gameRepository.findByTeamId(teamId);
    }

    public List<Game> getHeadToHeadMatches(int team1Id, int team2Id) {
        return gameRepository.findHeadToHeadMatches(team1Id, team2Id);
    }
}
