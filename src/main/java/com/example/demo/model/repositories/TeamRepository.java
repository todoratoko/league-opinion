package com.example.demo.model.repositories;

import com.example.demo.model.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(exported = false)
public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByPandascoreTeamId(Long pandascoreTeamId);
    List<Team> findByName(String name);
    List<Team> findByTag(String tag);
}
