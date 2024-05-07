package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.Match;
import com.example.demo.model.entities.Team;
import com.example.demo.model.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MatchService {
    @Autowired
    MatchRepository matchRepository;

    public Match getById(long id) {
        Optional<Match> match = matchRepository.findById(id);
        if(match.isPresent()){
            return match.get();
        }else{
            throw new NotFoundException("Match not found");
        }

    }
}
