package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.example.patrycja.filmbase.exception.AlreadyUpToDateException;
import com.example.patrycja.filmbase.exception.DuplicateException;
import com.example.patrycja.filmbase.exception.FilmDoesntExistException;
import com.example.patrycja.filmbase.exception.InvalidParamException;
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
        for (FilmBriefDTO filmBrief : actorRequest.getFilms()) {
            Film film = filmRepository.findByTitleAndProductionYear(
                    filmBrief.getTitle(),
                    filmBrief.getProductionYear());
            if (film == null) {
                throw new FilmDoesntExistException("Such film doesn't exist!");
            }
            films.add(film);
        }
        Actor actor = new Actor(actorRequest.getFirstName(),
                actorRequest.getLastName(),
                actorRequest.getDateOfBirth(),
                films);
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
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("actors/{id}") //TODO: test
    @ResponseStatus(HttpStatus.OK)
    public ActorDTO updateActor(@RequestParam(value = "action") String action,
                                @RequestParam(value = "rating", required = false) Double rating,
                                @RequestParam(value = "birthday", required = false) String dateOfBirth,
                                @PathVariable("id") long id) {

        Actor actor = actorRepository.findById(id);
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

            if (user.getRatedActors().containsKey(actor.getId())) {
                throw new DuplicateException("You've already rated this actor!");
            } else if ((rating < 0) || (rating > 10)) { //TODO: action when rating==null
                throw new InvalidParamException("Your rating must fall between 0 and 10!");
            }
            actor.rate(rating);
            actorRepository.save(actor);
            user.getRatedActors().put(actor.getId(), rating);
            userRepository.save(user);
            return new ActorDTO(actor);

        } else if (action.equalsIgnoreCase("update")) { //TODO: date validation

            if (actor.getDateOfBirth() != null) {
                throw new AlreadyUpToDateException("All actor data is up to date!");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            Actor updatedActor = new Actor(
                    actor.getFirstName(),
                    actor.getLastName(),
                    LocalDate.parse(dateOfBirth, formatter),
                    actor.getFilms());
            actorRepository.setDateOfBirthById(LocalDate.parse(dateOfBirth, formatter), id);
            ActorDTO actorDTO = new ActorDTO(updatedActor);
            actorDTO.setId(id);
            return actorDTO;

        } else {
            throw new InvalidParamException("Invalid action request!");
        }
    }

    @DeleteMapping("actors/{id}") //TODO: test
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
            actorRepository.getFilmsById(id)
                    .stream()
                    .forEach(film -> film.getCast().remove(actor));
            actorRepository.delete(actor);
            return HttpStatus.OK;
        }
        return HttpStatus.FORBIDDEN;
    }
}
