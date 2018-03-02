package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.DirectorDTO;
import com.example.patrycja.filmbase.exception.AlreadyUpToDateException;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.repository.DirectorRepository;
import com.example.patrycja.filmbase.repository.FilmRepository;
import com.example.patrycja.filmbase.request.UpdateDateOfBirthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DirectorController {

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    DirectorRepository directorRepository;

    @GetMapping("/directors")
    public List<DirectorDTO> findAllDirectors() {
        List<DirectorDTO> directorDTOs = new ArrayList<>();
        for(Director director : directorRepository.findAll()) {
            directorDTOs.add(new DirectorDTO(director));
        }
        return directorDTOs;
    }

    @GetMapping("/directors/{id}")
    public HttpEntity<DirectorDTO> findDirector(@PathVariable("id") long id) {
        Director director = directorRepository.findById(id);
        if(director!=null) {
            return ResponseEntity.ok(new DirectorDTO(director));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/directors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DirectorDTO updateDirector(@Valid @RequestBody UpdateDateOfBirthRequest dateOfBirthRequest,
                                      @PathVariable("id") long id) {
        Director director = directorRepository.findById(id);
        if(director.getDateOfBirth()!=null) {
            throw new AlreadyUpToDateException("All director's data is up to date!");
        }
        Director updatedDirector = new Director(
                director.getFirstName(),
                director.getLastName(),
                dateOfBirthRequest.getDateOfBirth(),
                director.getFilms()
        );
        directorRepository.setDateOfBirthById(dateOfBirthRequest.getDateOfBirth(), id);
        DirectorDTO directorDTO = new DirectorDTO(updatedDirector);
        directorDTO.setId(id);
        return directorDTO;
    }
}
