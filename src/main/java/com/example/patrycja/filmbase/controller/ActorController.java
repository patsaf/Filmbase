package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.example.patrycja.filmbase.exception.AlreadyUpToDateException;
import com.example.patrycja.filmbase.exception.DuplicateException;
import com.example.patrycja.filmbase.exception.FilmDoesntExistException;
import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Film;
import com.example.patrycja.filmbase.repository.ActorRepository;
import com.example.patrycja.filmbase.repository.FilmRepository;
import com.example.patrycja.filmbase.request.AddActorRequest;
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
        actorRepository.findAll()
                .forEach(actor -> allDTOs.add(new ActorDTO(actor)));
        return allDTOs;
    }

    @PostMapping("/actors")
    @ResponseStatus(HttpStatus.CREATED)
    public ActorDTO addNewActor(@Valid @RequestBody AddActorRequest actorRequest) {
        if(actorRepository.findByFirstNameAndLastName(
                actorRequest.getFirstName(),
                actorRequest.getLastName()) != null) {
            throw new DuplicateException("Actor already exists!");
        }
        List<Film> films = new ArrayList<>();
        for(FilmBriefDTO filmBrief  : actorRequest.getFilms()) {
            Film film = filmRepository.findByTitleAndProductionYear(
                    filmBrief.getTitle(),
                    filmBrief.getProductionYear());
            if(film==null) {
                throw new FilmDoesntExistException("Such film doesn't exist!");
            }
            films.add(film);
        }
        Actor actor = new Actor(actorRequest.getFirstName(),
                actorRequest.getLastName(),
                actorRequest.getDateOfBirth(),
                films);
        actorRepository.save(actor);
        for(Film film : films) {
            List<Actor> cast = film.getCast();
            cast.add(actor);
            filmRepository.save(film);
        }
        return new ActorDTO(actor);
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
