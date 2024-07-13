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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpinionServiceTests {
    @InjectMocks
    private OpinionService opinionService;
    @Mock
    private OpinionRepository opinionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private GameRepository gameRepository;
    private AddOpinionDTO addOpinionDTO;
    private Game game;
    private User ownerUser;
    private Opinion opinion;
    private OpinionWithOwnerDTO opinionWithOwnerDTO;

    @BeforeEach
    void setUp() {
        long gameId = 1;
        game = new Game();
        game.setId(gameId);

        opinion = new Opinion();
        opinion.setOwner(ownerUser);
        opinion.setGame(game);
        opinion.setId(1L);

        opinionWithOwnerDTO = new OpinionWithOwnerDTO();
        opinionWithOwnerDTO.setGame(game);
        opinionWithOwnerDTO.setTeamOnePercent(60);
        opinionWithOwnerDTO.setTeamTwoPercent(40);
        opinionWithOwnerDTO.setOpinion("I think faker wrist is swollen");
    }

    @Test
    @DisplayName("Test add opinion")
    void shouldAddOpinion() {
        long userId = 109;
        long gameId = 1;

        ownerUser = new User();
        ownerUser.setOpinions(new HashSet<>());
        ownerUser.setId(userId);

        addOpinionDTO = new AddOpinionDTO();
        addOpinionDTO.setTeamOnePercent(60);
        addOpinionDTO.setTeamTwoPercent(40);
        addOpinionDTO.setOpinion("I think faker wrist is swollen");


        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userRepository.findById(userId)).thenReturn(Optional.of(ownerUser));
        when(modelMapper.map(addOpinionDTO, Opinion.class)).thenReturn(opinion);
        when(opinionRepository.save(opinion)).thenReturn(opinion);
        when(modelMapper.map(opinion, OpinionWithOwnerDTO.class)).thenReturn(opinionWithOwnerDTO);

        OpinionWithOwnerDTO result = opinionService.addOpinion(addOpinionDTO, gameId, userId);

        assertNotNull(result);
        assertEquals(game.getId(), result.getGame().getId());

        verify(gameRepository, times(1)).findById(gameId);
        verify(userRepository, times(1)).findById(userId);
        verify(opinionRepository, times(1)).save(opinion);
        verify(userRepository, times(1)).save(ownerUser);
        verify(modelMapper, times(1)).map(addOpinionDTO, Opinion.class);
        verify(modelMapper, times(1)).map(opinion, OpinionWithOwnerDTO.class);
    }

    @Test
    void shouldReturnOpinionWithOwnerDTOWhenOpinionExists() {
        when(opinionRepository.findById(opinion.getId())).thenReturn(Optional.of(opinion));
        when(modelMapper.map(opinion, OpinionWithOwnerDTO.class)).thenReturn(opinionWithOwnerDTO);

        OpinionWithOwnerDTO result = opinionService.getById(opinion.getId());

        assertNotNull(result);
        assertEquals(opinionWithOwnerDTO.getId(), result.getId());

        verify(opinionRepository, times(1)).findById(opinion.getId());
        verify(modelMapper, times(1)).map(opinion, OpinionWithOwnerDTO.class);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenOpinionDoesNotExist() {
        when(opinionRepository.findById(game.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> opinionService.getById(Long.valueOf(opinion.getId())));

        verify(opinionRepository, times(1)).findById(opinion.getId());
        verify(modelMapper, times(0)).map(any(), any());

    }

}
