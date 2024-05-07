package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.Opinion;
import com.example.demo.model.entities.Player;
import com.example.demo.model.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {
    PlayerRepository playerRepository;

    public Player getById(long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            return player.get();
        } else {
            throw new NotFoundException("Player not found");
        }
    }
}