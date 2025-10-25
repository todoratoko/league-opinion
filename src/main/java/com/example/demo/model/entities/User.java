package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String profileImage;
    @Column
    private String email;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDate lastLogin;
    @Column
    private Integer loginAttempts; //ToDo
    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "portfolio_size")
    private Double portfolioSize;  // Amount of money in user's account

    @Column(name = "min_edge")
    private Double minEdge;  // Minimum edge threshold as percentage (e.g., 5.0 for 5%)

    @Column(name = "min_win")
    private Double minWin;  // Minimum win as percentage of portfolio (e.g., 2.0 for 2%)

    @OneToMany(mappedBy = "owner")
    private Set<Opinion> opinions;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_follow_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private Set<User> following;

    @ManyToMany(mappedBy = "following")
    private Set<User> followers;

}
