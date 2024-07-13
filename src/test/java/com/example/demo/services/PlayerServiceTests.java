package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.Game;
import com.example.demo.model.entities.Player;
import com.example.demo.model.repositories.GameRepository;
import com.example.demo.model.repositories.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTests {
    @InjectMocks
    PlayerService playerService;
    @Mock
    PlayerRepository playerRepository;
    Player player;

    @BeforeEach
    void setUp(){
        player = new Player();
        player.setId(1);
    }
    @Test
    void shouldReturnChampionshipWhenExists() {
        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));

        Player result = playerService.getById(player.getId());
        assertNotNull(result);
        assertEquals(player.getId(), result.getId());

        verify(playerRepository, times(1)).findById(player.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenChampionshipDoesNotExist() {
        when(playerRepository.findById(player.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> playerService.getById(player.getId()));

        verify(playerRepository, times(1)).findById(player.getId());

    }
}
