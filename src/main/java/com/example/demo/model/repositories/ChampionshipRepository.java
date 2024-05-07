package com.example.demo.model.repositories;

import com.example.demo.model.entities.Championship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChampionshipRepository extends JpaRepository<Championship, Long> {
}
