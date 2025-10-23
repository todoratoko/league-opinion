package com.example.demo.model.repositories;

import com.example.demo.model.entities.Opinion;
import com.example.demo.model.entities.OpinionLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpinionLikeRepository extends JpaRepository<OpinionLike, Long> {

    @Query("SELECT ol.opinion FROM OpinionLike ol WHERE ol.user.id = :userId")
    List<Opinion> findOpinionsByUserId(@Param("userId") Long userId);
}
