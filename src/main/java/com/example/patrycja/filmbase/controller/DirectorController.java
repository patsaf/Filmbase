package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.DirectorDTO;
import com.example.patrycja.filmbase.exception.AlreadyUpToDateException;
import com.example.patrycja.filmbase.exception.DuplicateException;
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
        List<DirectorDTO> directorDTOs = new ArrayList<>();
        directorRepository.findAll()
                .forEach(director -> directorDTOs.add(new DirectorDTO(director)));
        return directorDTOs;
    }

    @GetMapping("/directors/{id}")
    public HttpEntity<DirectorDTO> findDirector(@PathVariable("id") long id) {
        Director director = directorRepository.findById(id);
        if (director != null) {
            return ResponseEntity.ok(new DirectorDTO(director));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/directors/{id}")
    @ResponseStatus(HttpStatus.OK) //TODO: test
    public DirectorDTO updateDirector(@RequestParam(value = "action") String action,
                                      @RequestParam(value = "rating", required = false) Double rating,
                                      @RequestParam(value = "birthday", required = false) String dateOfBirth,
                                      @PathVariable("id") long id) {

        Director director = directorRepository.findById(id);
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUsername(username);

        if (action.equalsIgnoreCase("favourite")) {

            if (user.getFavDirectors().contains(director)) {
                throw new DuplicateException("This director is already on your list!");
            }
            user.getFavDirectors().add(director);
            userRepository.save(user);
            return new DirectorDTO(director);

        } else if (action.equalsIgnoreCase("rate")) {

            if (user.getRatedActors().containsKey(director.getId())) {
                throw new DuplicateException("You've already rated this actor!");
            } else if ((rating < 0) || (rating > 10)) { //TODO: action when rating==null
                throw new InvalidParamException("Your rating must fall between 0 and 10!");
            }
            director.rate(rating);
            directorRepository.save(director);
            user.getRatedActors().put(director.getId(), rating);
            userRepository.save(user);
            return new DirectorDTO(director);

        } else if (action.equalsIgnoreCase("update")) { //TODO: date validation

            if (director.getDateOfBirth() != null) {
                throw new AlreadyUpToDateException("All director's data is up to date!");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            Director updatedDirector = new Director(
                    director.getFirstName(),
                    director.getLastName(),
                    LocalDate.parse(dateOfBirth, formatter),
                    director.getFilms()
            );
            directorRepository.setDateOfBirthById(LocalDate.parse(dateOfBirth, formatter), id);
            DirectorDTO directorDTO = new DirectorDTO(updatedDirector);
            directorDTO.setId(id);
            return directorDTO;

        } else {
            throw new InvalidParamException("Invalid action request!");
        }
    }
}
