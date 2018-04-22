package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.UserDTO;
import com.example.patrycja.filmbase.exception.DuplicateException;
import com.example.patrycja.filmbase.exception.InvalidIdException;
import com.example.patrycja.filmbase.model.User;
import com.example.patrycja.filmbase.repository.UserRepository;
import com.example.patrycja.filmbase.request.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostConstruct
    public void introduceAdmin() {
        User admin = new User.UserBuilder("admin")
                .password("hardpass")
                .email("admin@gmail.com")
                .registerDate(LocalDate.now())
                .build();
        admin.makeAdmin();
        userRepository.save(admin);
    }

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
        List<UserDTO> allUserDTOs = new ArrayList<>();
        userRepository.findAll()
                .forEach(user -> allUserDTOs.add(new UserDTO(user)));
        return allUserDTOs;
    }

    @GetMapping("/users/{id}")
    public HttpEntity<UserDTO> findUser(@PathVariable("id") long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            return ResponseEntity.ok(new UserDTO(user));
        }
        throw new InvalidIdException();
    }

    @PostMapping("/users/{id}")
    public HttpEntity<UserDTO> upgradeToAdmin(@PathVariable("id") long id) {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        if (userRepository
                .findByUsername(username)
                .isAdmin()) {
            User user = userRepository.findById(id);

            if(user==null) {
                throw new InvalidIdException();
            }

            user.makeAdmin();
            userRepository.save(user);
            return ResponseEntity.ok(new UserDTO(user));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
