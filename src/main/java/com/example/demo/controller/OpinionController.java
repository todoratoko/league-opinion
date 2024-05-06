package com.example.demo.controller;

import com.example.demo.model.repositories.OpinionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpinionController {
    @Autowired
    private OpinionRepository opinionRepository;




}
