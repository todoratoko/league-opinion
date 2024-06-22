package com.example.demo.model.repositories;

import com.example.demo.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findUserByLastLoginBefore(LocalDate thirtyDaysAgo);
}
