package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.Team;
import com.example.demo.model.repositories.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTests {
    @InjectMocks
    TeamService teamService;
    @Mock
    TeamRepository teamRepository;
    Team team;

    @BeforeEach
    public void setUp() {
        team = new Team();
        team.setId(1);
    }

    @Test
    public void shouldReturnTeamWhenExists() {
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));

        Team result = teamService.getById(team.getId());
        assertNotNull(result);
        assertEquals(result.getId(), team.getId());

        verify(teamRepository, times(1)).findById(team.getId());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenTeamDoesNotExist() {
        when(teamRepository.findById(team.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> teamService.getById(team.getId()));

        verify(teamRepository, times(1)).findById(team.getId());
    }


}
