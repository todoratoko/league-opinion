package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.Game;
import com.example.demo.model.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    @Autowired
    GameRepository gameRepository;

    public Game getById(long id) {
        Optional<Game> game = gameRepository.findById(id);
        if(game.isPresent()){
            return game.get();
        }else{
            throw new NotFoundException("Match not found");
        }
    }

    public List<Game> getGamesByRegionId(Long regionId) {
        return gameRepository.findByRegionId(regionId);
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }
}
