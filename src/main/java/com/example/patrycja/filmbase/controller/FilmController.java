package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.exception.DuplicateException;
import com.example.patrycja.filmbase.exception.InvalidParamException;
import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.model.Film;
import com.example.patrycja.filmbase.model.User;
import com.example.patrycja.filmbase.repository.ActorRepository;
import com.example.patrycja.filmbase.repository.DirectorRepository;
import com.example.patrycja.filmbase.repository.FilmRepository;
import com.example.patrycja.filmbase.repository.UserRepository;
import com.example.patrycja.filmbase.request.AddActorRequest;
import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.example.patrycja.filmbase.service.FilmGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FilmController {

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    DirectorRepository directorRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    UserRepository userRepository;

    @PostConstruct
    public void initialize() {
        FilmGenerator filmGenerator = new FilmGenerator();
        for (int i = 0; i < filmGenerator.getCount(); i++) {
            Director director = directorRepository.findByFirstNameAndLastName(
                    filmGenerator.getFilm(i).getDirector().getFirstName(),
                    filmGenerator.getFilm(i).getDirector().getLastName());

            if (director == null) {
                directorRepository.save(filmGenerator.getFilm(i).getDirector());
            }

            List<Actor> cast = filmGenerator.getFilm(i).getCast();
            for (int j = 0; j < cast.size(); j++) {
                Actor actor = actorRepository.findByFirstNameAndLastName(
                        cast.get(j).getFirstName(),
                        cast.get(j).getLastName());
                if (actor == null) {
                    actorRepository.save(cast.get(j));
                }
            }
            filmRepository.save(filmGenerator.getFilm(i));
        }
    }

    @GetMapping("/films")
    public List<FilmDTO> getAllFilms() {
        List<FilmDTO> allDTOs = new ArrayList<>();
        filmRepository.findAll()
                .forEach(film -> allDTOs.add(new FilmDTO(film)));
        return allDTOs;
    }

    @GetMapping("/films/{id}")
    public HttpEntity<FilmDTO> findFilm(@PathVariable("id") long id) {
        Film film = filmRepository.findById(id);
        if (film != null) {
            return ResponseEntity.ok(new FilmDTO(film));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/films/{id}")
    public HttpEntity<FilmDTO> processFilm(@RequestParam(value = "action") String action,
                                           @RequestParam(value = "rating", required = false) Double rating,
                                           @PathVariable("id") long id) {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUsername(username);
        Film film = filmRepository.findById(id);
        if (action.equalsIgnoreCase("favourite")) {
            if (user.getFavFilms().contains(film)) {
                throw new DuplicateException("This film is already on your list!");
            }
            user.getFavFilms().add(film);
            userRepository.save(user);
        } else if (action.equalsIgnoreCase("wishlist")) {
            if (user.getFilmWishlist().contains(film)) {
                throw new DuplicateException("This film is already on your list!");
            }
            user.getFilmWishlist().add(film);
            userRepository.save(user);
        } else if (action.equalsIgnoreCase("rate")) {
            if ((rating < 0) || (rating > 10)) { //TODO: action when rating==null
                throw new InvalidParamException("Your rating must fall between 0 and 10!");
            } else if (user.getRatedFilms().containsKey(film.getId())) {
                throw new DuplicateException("You've already rated this film!");
            }
            film.rate(rating);
            filmRepository.save(film);
            user.getRatedFilms().put(film.getId(), rating);
            userRepository.save(user);
        } else {
            throw new InvalidParamException("Invalid action request!");
        }
        return ResponseEntity.ok(new FilmDTO(film));
    }

    @DeleteMapping("/films/{id}")
    public HttpStatus deleteFilm(@PathVariable("id") long id) {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        if (userRepository
                .findByUsername(username)
                .isAdmin()) {
            Film film = filmRepository.findById(id);
            List<Actor> castById = filmRepository.getCastById(id);
            filmRepository.delete(film);
            if (film
                    .getDirector()
                    .getFilms()
                    .isEmpty()) {
                directorRepository.delete(film.getDirector());
            }
            for (Actor actor : castById) {
                if (actor.getFilms().isEmpty()) {
                    actorRepository.delete(actor);
                }
            }
            return HttpStatus.OK;
        }
        return HttpStatus.FORBIDDEN;
    }

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDTO addFilm(@Valid @RequestBody AddFilmRequest addFilmRequest) {

        Director director = directorRepository.findByFirstNameAndLastName(
                addFilmRequest.getDirectorFirstName(),
                addFilmRequest.getDirectorLastName());

        if (director == null) {
            director = new Director(
                    addFilmRequest.getDirectorFirstName(),
                    addFilmRequest.getDirectorLastName());
            directorRepository.save(director);
        }

        List<Actor> cast = new ArrayList<>();
        for (AddActorRequest actorRequest : addFilmRequest.getActorRequests()) {
            Actor actor = actorRepository.findByFirstNameAndLastName(
                    actorRequest.getFirstName(),
                    actorRequest.getLastName());

            if (actor == null) {
                actor = new Actor(
                        actorRequest.getFirstName(),
                        actorRequest.getLastName());
                actorRepository.save(actor);
            }
            cast.add(actor);
        }

        Film newFilm = new Film(
                addFilmRequest.getTitle(),
                director,
                addFilmRequest.getTypes(),
                addFilmRequest.getProductionYear(),
                cast);

        for (Film film : filmRepository.findAll()) {
            if (newFilm.checkIfContentEquals(film)) {
                throw new DuplicateException("Film already exists!");
            }
        }
        filmRepository.save(newFilm);
        return new FilmDTO(newFilm);
    }

    @GetMapping("/films/{id}/cast")
    public List<ActorDTO> getFilmCast(@PathVariable("id") long id) {
        Film film = filmRepository.findById(id);
        List<ActorDTO> actorDTOS = new ArrayList<>();
        film.getCast()
                .forEach(actor -> actorDTOS.add(new ActorDTO(actor)));
        return actorDTOS;
    }

    @PostMapping("/films/{id}/cast")
    public List<ActorDTO> addActorToCast(@Valid @RequestBody AddActorRequest actorRequest,
                                         @PathVariable("id") long id) {
        Film film = filmRepository.findById(id);
        Actor actor = actorRepository.findByFirstNameAndLastName(
                actorRequest.getFirstName(),
                actorRequest.getLastName());
        Actor finalActor = actor;
        if (actor == null) {
            List<Film> films = new ArrayList<>();
            films.add(film);
            actor = new Actor(actorRequest.getFirstName(),
                    actorRequest.getLastName(),
                    actorRequest.getDateOfBirth(),
                    films);
        } else if (film.getCast()
                .stream()
                .anyMatch(oldActor -> oldActor.getFirstName().equals(finalActor.getFirstName()) &&
                        oldActor.getLastName().equals(finalActor.getLastName()))) {
            throw new DuplicateException("Actor already in cast!");
        }
        actorRepository.save(actor);
        List<Actor> cast = film.getCast();
        cast.add(actor);
        filmRepository.save(film);
        List<ActorDTO> actorDTOS = new ArrayList<>();
        for (Actor actor1 : cast) {
            actorDTOS.add(new ActorDTO(actor1));
        }
        return actorDTOS;
    }
}
