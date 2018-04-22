package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.exception.DuplicateException;
import com.example.patrycja.filmbase.exception.InvalidIdException;
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
                    filmGenerator.getDirectorFirstName(i),
                    filmGenerator.getDirectorLastName(i));
            if (director == null) {
                directorRepository.save(filmGenerator.getDirector(i));
            }

            List<Actor> cast = filmGenerator.getFilmCast(i);
            for (int j = 0; j < cast.size(); j++) {
                Actor actor = actorRepository.findByFirstNameAndLastName(
                        filmGenerator.getActorFromCast(i, j).getFirstName(),
                        filmGenerator.getActorFromCast(i, j).getLastName());
                if (actor == null) {
                    actorRepository.save(filmGenerator.getActorFromCast(i, j));
                }
            }
            filmRepository.save(filmGenerator.getFilm(i));
        }
    }

    @GetMapping("/films")
    public List<FilmDTO> getAllFilms() {
        List<FilmDTO> allFilmDTOs = new ArrayList<>();
        filmRepository.findAll()
                .forEach(film -> allFilmDTOs.add(new FilmDTO(film)));
        return allFilmDTOs;
    }

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDTO addFilm(@Valid @RequestBody AddFilmRequest addFilmRequest) {

        Director director = directorRepository.findByFirstNameAndLastName(
                addFilmRequest.getDirectorFirstName(),
                addFilmRequest.getDirectorLastName());

        if (director == null) {
            director = new Director.DirectorBuilder(
                    addFilmRequest.getDirectorFirstName(),
                    addFilmRequest.getDirectorLastName())
                    .build();
            directorRepository.save(director);
        }

        List<Actor> cast = new ArrayList<>();
        if (addFilmRequest.getActorRequests() != null) {
            for (AddActorRequest actorRequest : addFilmRequest.getActorRequests()) {
                Actor actor = actorRepository.findByFirstNameAndLastName(
                        actorRequest.getFirstName(),
                        actorRequest.getLastName());

                if (actor == null) {
                    actor = new Actor.ActorBuilder(
                            actorRequest.getFirstName(),
                            actorRequest.getLastName())
                            .build();
                    actorRepository.save(actor);
                }
                cast.add(actor);
            }
        }

        Film newFilm = new Film.FilmBuilder(addFilmRequest.getTitle())
                .director(director)
                .types(addFilmRequest.getTypes())
                .productionYear(addFilmRequest.getProductionYear())
                .cast(cast)
                .build();

        for (Film film : filmRepository.findAll()) {
            if (newFilm.checkIfContentEquals(film)) {
                throw new DuplicateException("Film already exists!");
            }
        }
        filmRepository.save(newFilm);
        return new FilmDTO(newFilm);
    }

    @GetMapping("/films/{id}")
    public HttpEntity<FilmDTO> findFilm(@PathVariable("id") long id) {
        Film film = filmRepository.findById(id);
        if (film != null) {
            return ResponseEntity.ok(new FilmDTO(film));
        }
        throw new InvalidIdException();
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

        if (film == null) {
            throw new InvalidIdException();
        }

        if (action.equalsIgnoreCase("favourite")) {

            if (user.checkIfFavFilmsContainFilm(film)) {
                throw new DuplicateException("This film is already on your list!");
            } else if (user.checkIfFilmWishlistContainsFilm(film)) {
                user.removeFilmFromWishlist(film);
            }
            user.addFavFilm(film);
            userRepository.save(user);

        } else if (action.equalsIgnoreCase("wishlist")) {

            if (user.checkIfFilmWishlistContainsFilm(film)) {
                throw new DuplicateException("This film is already on your list!");
            } else if (user.checkIfFavFilmsContainFilm(film)) {
                throw new DuplicateException("Seems like you've already seen this film!");
            }
            user.addFilmToWishlist(film);
            userRepository.save(user);

        } else if (action.equalsIgnoreCase("rate")) {

            try {
                if ((rating < 0) || (rating > 10)) {
                    throw new InvalidParamException("Your rating must fall between 0 and 10!");
                } else if (user.checkIfFilmAlreadyRated(film)) {
                    throw new DuplicateException("You've already rated this film!");
                }
                film.rate(rating);
                filmRepository.save(film);
                user.addRatedFilm(film.getId(), rating);
                userRepository.save(user);
            } catch (NullPointerException ex) {
                throw new InvalidParamException("You need to insert your rating!");
            }

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

            if (film == null) {
                throw new InvalidIdException();
            }

            List<Actor> castById = filmRepository.getCastById(id);
            filmRepository.delete(film);
            if (!film
                    .getDirector()
                    .hasAnyFilms()) {
                directorRepository.delete(film.getDirector());
            }
            for (Actor actor : castById) {
                if (!actor.hasAnyFilms()) {
                    actorRepository.delete(actor);
                }
            }
            return HttpStatus.OK;
        }
        return HttpStatus.FORBIDDEN;
    }
}
