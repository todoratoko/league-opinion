package com.example.demo.model.repositories;

import com.example.demo.model.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByPandascorePlayerId(Long pandascorePlayerId);
}
