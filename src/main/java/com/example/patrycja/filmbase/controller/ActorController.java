package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.example.patrycja.filmbase.exception.*;
import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Film;
import com.example.patrycja.filmbase.model.User;
import com.example.patrycja.filmbase.repository.ActorRepository;
import com.example.patrycja.filmbase.repository.FilmRepository;
import com.example.patrycja.filmbase.repository.UserRepository;
import com.example.patrycja.filmbase.request.AddActorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ActorController {

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    UserRepository userRepository;

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
        if (actorRepository.findByFirstNameAndLastName(
                actorRequest.getFirstName(),
                actorRequest.getLastName()) != null) {
            throw new DuplicateException("Actor already exists!");
        }
        List<Film> films = new ArrayList<>();
        if(actorRequest.getFilms()==null) {
            throw new FilmDoesntExistException("What kind of actor has no filmography?");
        }
        for (FilmBriefDTO filmBrief : actorRequest.getFilms()) {
            Film film = filmRepository.findByTitleAndProductionYear(
                    filmBrief.getTitle(),
                    filmBrief.getProductionYear());
            if (film == null) {
                throw new FilmDoesntExistException("Such film doesn't exist!");
            }
            films.add(film);
        }
        Actor actor = new Actor.ActorBuilder(
                actorRequest.getFirstName(),
                actorRequest.getLastName())
                .dateOfBirth(actorRequest.getDateOfBirth())
                .films(films)
                .build();
        actorRepository.save(actor);
        for (Film film : films) {
            List<Actor> cast = film.getCast();
            cast.add(actor);
            filmRepository.save(film);
        }
        return new ActorDTO(actor);
    }

    @GetMapping("actors/{id}")
    public HttpEntity<ActorDTO> findActor(@PathVariable("id") long id) {
        Actor actor = actorRepository.findById(id);
        if (actor != null) {
            return ResponseEntity.ok(new ActorDTO(actor));
        }
        throw new InvalidIdException();
    }

    @PostMapping("actors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ActorDTO updateActor(@RequestParam(value = "action") String action,
                                @RequestParam(value = "rating", required = false) Double rating,
                                @RequestParam(value = "birthday", required = false) String birthday,
                                @PathVariable("id") long id) {

        Actor actor = actorRepository.findById(id);

        if (actor == null) {
            throw new InvalidIdException();
        }

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUsername(username);

        if (action.equalsIgnoreCase("favourite")) {

            if (user.getFavActors().contains(actor)) {
                throw new DuplicateException("This actor is already on your list!");
            }
            user.getFavActors().add(actor);
            userRepository.save(user);
            return new ActorDTO(actor);

        } else if (action.equalsIgnoreCase("rate")) {

            try {
                if ((rating < 0) || (rating > 10)) {
                    throw new InvalidParamException("Your rating must fall between 0 and 10!");
                } else if (user.getRatedActors().containsKey(actor.getId())) {
                    throw new DuplicateException("You've already rated this actor!");
                }
                actor.rate(rating);
                actorRepository.save(actor);
                user.getRatedActors().put(actor.getId(), rating);
                userRepository.save(user);
                return new ActorDTO(actor);
            } catch (NullPointerException ex) {
                throw new InvalidParamException("You need to insert your rating!");
            }

        } else if (action.equalsIgnoreCase("update")) {

            if (actor.getDateOfBirth() != null) {
                throw new AlreadyUpToDateException("All actor data is up to date!");
            }
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                Actor updatedActor = new Actor.ActorBuilder(
                        actor.getFirstName(),
                        actor.getLastName())
                        .dateOfBirth(LocalDate.parse(birthday, formatter))
                        .films(actor.getFilms())
                        .build();
                actorRepository.setDateOfBirthById(LocalDate.parse(birthday, formatter), id);
                ActorDTO actorDTO = new ActorDTO(updatedActor);
                actorDTO.setId(id);
                return actorDTO;
            } catch (NullPointerException ex) {
                throw new InvalidParamException("You need to insert date!");
            } catch (DateTimeParseException dex) {
                throw new InvalidParamException("Invalid date format! The pattern should be: \"dd-MM-yyyy\"");
            }

        } else {
            throw new InvalidParamException("Invalid action request!");
        }
    }

    @DeleteMapping("actors/{id}")
    public HttpStatus deleteActor(@PathVariable("id") long id) {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        if (userRepository
                .findByUsername(username)
                .isAdmin()) {
            Actor actor = actorRepository.findById(id);

            if (actor == null) {
                throw new InvalidIdException();
            }

            actorRepository.getFilmsById(id)
                    .stream()
                    .forEach(film -> film.getCast().remove(actor));
            actorRepository.delete(actor);
            return HttpStatus.OK;
        }
        return HttpStatus.FORBIDDEN;
    }
}
