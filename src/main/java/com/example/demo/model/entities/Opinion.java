package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Table(name = "opinion")
public class Opinion {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
@Column
    private Date created_at;   //Java Date => mySQL datetime?
@Column
    private int team_one_win_percent;
@Column
    private int team_two_win_percent;
@Column
    private long user_id;
}
