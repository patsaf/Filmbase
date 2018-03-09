package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.UserDTO;
import com.example.patrycja.filmbase.exception.DuplicateException;
import com.example.patrycja.filmbase.model.User;
import com.example.patrycja.filmbase.repository.UserRepository;
import com.example.patrycja.filmbase.request.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()) != null) {
            throw new DuplicateException("User with given email already exists!");
        } else if (userRepository.findByUsername(signUpRequest.getUsername()) != null) {
            throw new DuplicateException("User with given username already exists!");
        }
        userRepository.save(signUpRequest.getUser());
    }

    @GetMapping("/users")
    public List<UserDTO> findAllUsers() {
        List<UserDTO> allDTOs = new ArrayList<>();
        userRepository.findAll()
                .forEach(user -> allDTOs.add(new UserDTO(user)));
        return allDTOs;
    }

    @GetMapping("/users/{id}")
    public HttpEntity<UserDTO> findUser(@PathVariable("id") long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            return ResponseEntity.ok(new UserDTO(user));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
