package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String username;
    @Column
    private String password;
    @Column//(name = "profile_image_url")
    private String image;
    @Column
    private String email;
    @OneToMany(mappedBy = "owner")
    private Set<Opinion> opinions;


}
