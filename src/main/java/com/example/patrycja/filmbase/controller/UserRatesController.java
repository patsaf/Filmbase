package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.maps.MapActorItemDTO;
import com.example.patrycja.filmbase.DTO.maps.MapDirectorItemDTO;
import com.example.patrycja.filmbase.DTO.maps.MapFilmItemDTO;
import com.example.patrycja.filmbase.DTO.maps.RatesDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserRatesController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    DirectorRepository directorRepository;

    @GetMapping("/users/{id}/rates")
    public RatesDTO findAllUserRatings(@PathVariable("id") long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new InvalidIdException();
        }
        return createRatesToDisplay(user);
    }

    @DeleteMapping("/users/{id}/rates")
    public HttpEntity<RatesDTO> cancelRating(@PathVariable("id") long id,
                                             @RequestParam("type") String type,
                                             @RequestParam("itemId") long itemId) {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findById(id);
        if (user == null) {
            throw new InvalidIdException();
        }
        if (!userRepository
                .findByUsername(username)
                .equals(user)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (type.equalsIgnoreCase("film")) {
            if (!user.checkIfFilmAlreadyRated(filmRepository.findById(itemId))) {
                throw new InvalidParamException("You haven't rated this film!");
            }
            Film film = filmRepository.findById(itemId);
            film
                    .unrate(user
                            .getRatedFilms()
                            .get(itemId));
            filmRepository.save(film);
            user.removeRatedFilm(itemId);
        } else if (type.equalsIgnoreCase("actor")) {
            if (!user.checkIfActorAlreadyRated(actorRepository.findById(itemId))) {
                throw new InvalidParamException("You haven't rated this actor!");
            }
            Actor actor = actorRepository.findById(itemId);
            actor
                    .unrate(user
                            .getRatedActors()
                            .get(itemId));
            actorRepository.save(actor);
            user.removeRatedActor(itemId);
        } else if (type.equalsIgnoreCase("director")) {
            if (!user.checkIfDirectorAlreadyRated(directorRepository.findById(itemId))) {
                throw new InvalidParamException("You haven't rated this director!");
            }
            Director director = directorRepository.findById(itemId);
            director
                    .unrate(user
                            .getRatedDirectors()
                            .get(itemId));
            directorRepository.save(director);
            user.removeRatedDirector(itemId);
        } else {
            throw new InvalidParamException("Invalid argument!");
        }
        userRepository.save(user);
        return ResponseEntity.ok(createRatesToDisplay(user));
    }

    private RatesDTO createRatesToDisplay(User user) {
        List<MapActorItemDTO> ratedActors = new ArrayList<>();
        List<MapDirectorItemDTO> ratedDirectors = new ArrayList<>();
        List<MapFilmItemDTO> filmDTOList = new ArrayList<>();

        for (Long key : user
                .getRatedFilms()
                .keySet()) {
            filmDTOList.add(new MapFilmItemDTO(
                    filmRepository.findById(key),
                    user
                            .getRatedFilms()
                            .get(key)
            ));
        }

        for (Long key : user
                .getRatedActors()
                .keySet()) {
            ratedActors.add(new MapActorItemDTO(
                    actorRepository.findById(key),
                    user
                            .getRatedActors()
                            .get(key)
            ));
        }

        for (Long key : user
                .getRatedDirectors()
                .keySet()) {
            ratedDirectors.add(new MapDirectorItemDTO(
                    directorRepository.findById(key),
                    user
                            .getRatedDirectors()
                            .get(key)
            ));
        }
        return new RatesDTO(ratedActors, ratedDirectors, filmDTOList);
    }
}
