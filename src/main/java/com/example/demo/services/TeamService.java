package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.Team;
import com.example.demo.model.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeamService {
    @Autowired
    TeamRepository teamRepository;

    public Team getById(long id) {
        Optional<Team> team = teamRepository.findById(id);
        if(team.isPresent()){
            return team.get();
        }else{
            throw new NotFoundException("Team not found");
        }
    }
}
