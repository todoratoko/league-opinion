package com.example.demo.model.repositories;

import com.example.demo.model.entities.ConfirmationToken;
import com.example.demo.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ConfirmationRepository extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByToken(String token);

    @Transactional
    void deleteByUser(User user);
}
