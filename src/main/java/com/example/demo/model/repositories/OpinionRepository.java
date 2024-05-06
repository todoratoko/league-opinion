package com.example.demo.model.repositories;

import com.example.demo.model.entities.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpinionRepository extends JpaRepository<Opinion, Long> {

}
