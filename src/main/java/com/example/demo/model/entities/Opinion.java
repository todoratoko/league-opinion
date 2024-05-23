package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "opinions")
public class Opinion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private Date created_at;   //Java Date => mySQL datetime?
    @Column
    private int team_one_percent;
    @Column
    private int team_two_percent;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;



}
