package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.Championship;
import com.example.demo.model.entities.Game;
import com.example.demo.model.repositories.ChampionshipRepository;
import com.example.demo.model.repositories.GameRepository;
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
public class GameServiceTests {
    @InjectMocks
    GameService gameService;
    @Mock
    GameRepository gameRepository;
    Game game;

    @BeforeEach
    public void setUp(){
        game = new Game();
        game.setId(1);
    }
    @Test
    public void shouldReturnGameWhenExists() {
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

        Game result = gameService.getById(game.getId());
        assertNotNull(result);
        assertEquals(game.getId(), result.getId());

        verify(gameRepository, times(1)).findById(game.getId());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGameDoesNotExist() {
        when(gameRepository.findById(game.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gameService.getById(game.getId()));

        verify(gameRepository, times(1)).findById(game.getId());

    }
}
