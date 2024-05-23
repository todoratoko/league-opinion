package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.AddOpinionDTO;
import com.example.demo.model.dto.OpinionWithOwnerDTO;
import com.example.demo.model.entities.Opinion;
import com.example.demo.model.repositories.OpinionRepository;
import com.example.demo.model.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
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
    private ModelMapper modelMapper;


    public OpinionWithOwnerDTO getById(Integer id) {
        Optional<Opinion> opinion = opinionRepository.findById(Long.valueOf(id));
        if (opinion.isPresent()) {
            OpinionWithOwnerDTO dto =  modelMapper.map(opinion, OpinionWithOwnerDTO.class);
            return dto;
        } else {
            throw new NotFoundException("Opinion not found");
        }
    }

    public OpinionWithOwnerDTO addOpinion(AddOpinionDTO opinion, Integer id) {
        if(id == null){
            throw new UnauthorizedException("Please login");
        }
        Opinion opinionSave = modelMapper.map(opinion, Opinion.class);
        opinionSave.setOwner(userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new NotFoundException("Owner not found")));
        opinionRepository.save(opinionSave);
        OpinionWithOwnerDTO opinionWithOwnerDTO = modelMapper.map(opinion, OpinionWithOwnerDTO.class);
        return opinionWithOwnerDTO;
    }


}
