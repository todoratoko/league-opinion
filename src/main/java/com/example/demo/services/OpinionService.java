package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.Opinion;
import com.example.demo.model.repositories.OpinionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OpinionService {
    @Autowired
    OpinionRepository opinionRepository;


    public Opinion getById(long id) {
        Optional<Opinion> opinion = opinionRepository.findById(id);
        if (opinion.isPresent()) {
            return opinion.get();
        } else {
            throw new NotFoundException("Opinion not found");
        }
    }
}
