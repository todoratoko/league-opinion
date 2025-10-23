package com.example.demo.model.repositories;

import com.example.demo.model.entities.Opinion;
import com.example.demo.model.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpinionRepository extends JpaRepository<Opinion, Long> {
    List<Opinion> findByGameRegion(Region region);
    List<Opinion> findByGameRegionId(Long regionId);

    // Find opinions created by a specific user
    List<Opinion> findByOwnerId(Long userId);
}
