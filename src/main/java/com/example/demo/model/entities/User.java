package com.example.demo.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

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
