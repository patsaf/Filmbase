package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.exception.AlreadyUpToDateException;
import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.repository.ActorRepository;
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
public class ActorController {

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    ActorRepository actorRepository;

    @GetMapping("/actors")
    public List<ActorDTO> findAllActors() {
        List<ActorDTO> allDTOs = new ArrayList<>();
        for(Actor actor : actorRepository.findAll()) {
            allDTOs.add(new ActorDTO(actor));
        }
        return allDTOs;
    }

    @GetMapping("actors/{id}")
    public HttpEntity<ActorDTO> findActor(@PathVariable("id") long id) {
        Actor actor = actorRepository.findById(id);
        if(actor!=null) {
            return ResponseEntity.ok(new ActorDTO(actor));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("actors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ActorDTO updateActor(@Valid @RequestBody UpdateDateOfBirthRequest updateRequest,
                                @PathVariable("id") long id) {
        Actor actor = actorRepository.findById(id);
        if(actor.getDateOfBirth()!=null) {
            throw new AlreadyUpToDateException("All actor data is up to date!");
        }
        Actor updatedActor = new Actor(
                actor.getFirstName(),
                actor.getLastName(),
                updateRequest.getDateOfBirth(),
                actor.getFilms());
        actorRepository.setDateOfBirthById(updateRequest.getDateOfBirth(), id);
        ActorDTO actorDTO = new ActorDTO(updatedActor);
        actorDTO.setId(id);
        return actorDTO;
    }
}
