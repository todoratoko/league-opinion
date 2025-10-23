package com.example.demo.model.repositories;

import com.example.demo.model.entities.Opinion;
import com.example.demo.model.entities.OpinionSave;
import com.example.demo.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpinionSaveRepository extends JpaRepository<OpinionSave, Long> {

    @Query("SELECT os.opinion FROM OpinionSave os WHERE os.user.id = :userId")
    List<Opinion> findOpinionsByUserId(@Param("userId") Long userId);

    Optional<OpinionSave> findByOpinionAndUser(Opinion opinion, User user);

    boolean existsByOpinionAndUser(Opinion opinion, User user);
}
