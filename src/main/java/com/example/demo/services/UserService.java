package com.example.demo.services;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.dto.UserResponseDTO;
import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User login(String username, String password){
        if(username == null || username.isBlank()){
            throw new BadRequestException("User name is mandatory");
        }
        if(password == null || password.isBlank()){
            throw new BadRequestException("Password field is mandatory");
        }
        User u = userRepository.findByUsernameAndPassword(username, password);
        if(u == null){
            throw new UnauthorizedException("Wrong  credentials");
        }
        return u;
    }

    public User register(String username, String password, String confirmPassword){
        if(username == null || username.isBlank()){
            throw new BadRequestException("User name is mandatory");
        }
        if(username.length() <= 5){
            throw new BadRequestException("Username is too short, must be at least 6 symbols");
        }
        if(password == null || password.isBlank()){
            throw new BadRequestException("Password field is mandatory");
        }
        if(userRepository.findByUsername(username) != null){
            throw new BadRequestException("User already exists");
        }
        if(!password.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")){
            throw new BadRequestException("Password field requires other symbols");
        }
        if(!password.equals(confirmPassword)){
            throw new BadRequestException("Passwords mismatch");
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        userRepository.save(u);
        return u;
    }


    public User getById(long id) {
        Optional<User> opt = userRepository.findById(id);
        if(opt.isPresent()){
            return opt.get();
        }
        else{
            throw new NotFoundException("User not found");
        }
    }

    public User edit(User user) {
        Optional<User> opt = userRepository.findById(user.getId());
        if(opt.isPresent()){
            userRepository.save(user);
            //delete all opinions
            return user;
        }
        else{
            throw new NotFoundException("User not found");
        }

    }
}

