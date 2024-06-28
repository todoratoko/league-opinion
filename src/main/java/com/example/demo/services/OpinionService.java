package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.dto.AddOpinionDTO;
import com.example.demo.model.dto.OpinionWithOwnerDTO;
import com.example.demo.model.entities.Game;
import com.example.demo.model.entities.Opinion;
import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.GameRepository;
import com.example.demo.model.repositories.OpinionRepository;
import com.example.demo.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OpinionService {
    @Autowired
    OpinionRepository opinionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    private ModelMapper modelMapper;


    public OpinionWithOwnerDTO getById(Integer id) {
        Optional<Opinion> opinion = opinionRepository.findById(Long.valueOf(id));
        if (opinion.isPresent()) {
            OpinionWithOwnerDTO dto = modelMapper.map(opinion, OpinionWithOwnerDTO.class);
            return dto;
        } else {
            throw new NotFoundException("Opinion not found");
        }
    }



    public OpinionWithOwnerDTO addOpinion(AddOpinionDTO opinion, Long matchId, Long id) {
        Game game = gameRepository.findById(matchId).orElseThrow(() -> new NotFoundException("There is no such Match"));
        User ownerUser = userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new NotFoundException("Owner not found"));
        Opinion opinionSave = modelMapper.map(opinion, Opinion.class);
        opinionSave.setOwner(ownerUser);
        opinionSave.setGame(game);
        opinionRepository.save(opinionSave);
        ownerUser.getOpinions().add(opinionSave);
        userRepository.save(ownerUser);
        OpinionWithOwnerDTO opinionWithOwnerDTO = modelMapper.map(opinionSave, OpinionWithOwnerDTO.class);
        return opinionWithOwnerDTO;
    }


}
