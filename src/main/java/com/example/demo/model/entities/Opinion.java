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
    private Date createdAt;   //Java Date => mySQL datetime?
    @Column
    private int teamOnePercent;
    @Column
    private int teamTwoPercent;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;



}
