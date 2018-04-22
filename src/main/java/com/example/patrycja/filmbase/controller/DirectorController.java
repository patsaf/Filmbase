package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.DirectorDTO;
import com.example.patrycja.filmbase.exception.AlreadyUpToDateException;
import com.example.patrycja.filmbase.exception.DuplicateException;
import com.example.patrycja.filmbase.exception.InvalidIdException;
import com.example.patrycja.filmbase.exception.InvalidParamException;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.model.User;
import com.example.patrycja.filmbase.repository.DirectorRepository;
import com.example.patrycja.filmbase.repository.FilmRepository;
import com.example.patrycja.filmbase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DirectorController {

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    DirectorRepository directorRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/directors")
    public List<DirectorDTO> findAllDirectors() {
        List<DirectorDTO> allDirectorDTOs = new ArrayList<>();
        directorRepository.findAll()
                .forEach(director -> allDirectorDTOs.add(new DirectorDTO(director)));
        return allDirectorDTOs;
    }

    @GetMapping("/directors/{id}")
    public HttpEntity<DirectorDTO> findDirector(@PathVariable("id") long id) {
        Director director = directorRepository.findById(id);
        if (director != null) {
            return ResponseEntity.ok(new DirectorDTO(director));
        }
        throw new InvalidIdException();
    }

    @PostMapping("/directors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DirectorDTO updateDirector(@RequestParam(value = "action") String action,
                                      @RequestParam(value = "rating", required = false) Double rating,
                                      @RequestParam(value = "birthday", required = false) String dateOfBirth,
                                      @PathVariable("id") long id) {

        Director director = directorRepository.findById(id);

        if (director == null) {
            throw new InvalidIdException();
        }

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUsername(username);

        if (action.equalsIgnoreCase("favourite")) {

            if (user.checkIfFavDirectorsContainActor(director)) {
                throw new DuplicateException("This director is already on your list!");
            }
            user.addFavDirector(director);
            userRepository.save(user);
            return new DirectorDTO(director);

        } else if (action.equalsIgnoreCase("rate")) {

            try {
                if ((rating < 0) || (rating > 10)) {
                    throw new InvalidParamException("Your rating must fall between 0 and 10!");
                } else if (user.checkIfDirectorAlreadyRated(director)) {
                    throw new DuplicateException("You've already rated this director!");
                }
                director.rate(rating);
                directorRepository.save(director);
                user.addRatedDirector(director.getId(), rating);
                userRepository.save(user);
                return new DirectorDTO(director);
            } catch (NullPointerException ex) {
                throw new InvalidParamException("You need to insert your rating!");
            }

        } else if (action.equalsIgnoreCase("update")) {

            if (director.hasDateOfBirth()) {
                throw new AlreadyUpToDateException("All director's data is up to date!");
            }
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                Director updatedDirector = new Director.DirectorBuilder(
                        director.getFirstName(),
                        director.getLastName())
                        .dateOfBirth(LocalDate.parse(dateOfBirth, formatter))
                        .films(director.getFilms())
                        .build();
                directorRepository.setDateOfBirthById(LocalDate.parse(dateOfBirth, formatter), id);
                DirectorDTO directorDTO = new DirectorDTO(updatedDirector);
                directorDTO.setId(id);
                return directorDTO;
            } catch (NullPointerException ex) {
                throw new InvalidParamException("You need to insert date!");
            } catch (DateTimeParseException dex) {
                throw new InvalidParamException("Invalid date format! The pattern should be: \"dd-MM-yyyy\"");
            }

        } else {
            throw new InvalidParamException("Invalid action request!");
        }
    }
}
