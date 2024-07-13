package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.dto.OpinionWithOwnerDTO;
import com.example.demo.model.entities.Championship;
import com.example.demo.model.repositories.ChampionshipRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ChampionshipServiceTests {
    @InjectMocks
    ChampionshipService championshipService;
    @Mock
    ChampionshipRepository championshipRepository;
    Championship championship;

    @BeforeEach
    void setUp(){
        championship = new Championship();
        championship.setId(1);
    }
    @Test
    void shouldReturnChampionshipWhenExists() {
        when(championshipRepository.findById(championship.getId())).thenReturn(Optional.of(championship));

        Championship result = championshipService.getById(championship.getId());
        assertNotNull(result);
        assertEquals(championship.getId(), result.getId());

        verify(championshipRepository, times(1)).findById(championship.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenChampionshipDoesNotExist() {
        when(championshipRepository.findById(championship.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> championshipService.getById(championship.getId()));

        verify(championshipRepository, times(1)).findById(championship.getId());

    }


}


