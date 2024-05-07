package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.Championship;
import com.example.demo.model.repositories.ChampionshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChampionshipService {
    @Autowired
    ChampionshipRepository championshipRepository;

    public Championship getById(long id) {
        Optional<Championship> championship = championshipRepository.findById(id);
        if(championship.isPresent()){
            return championship.get();
        }else{
            throw new NotFoundException("Championship not found");
        }
    }
}
