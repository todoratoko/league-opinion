package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "opinion_likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"opinion_id", "user_id"}))
public class OpinionLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "opinion_id", nullable = false)
    private Opinion opinion;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private LocalDateTime createdAt;
}
