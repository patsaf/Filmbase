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
        if(user==null) {
            throw new InvalidIdException();
        }
        return createRatesToDisplay(user);
    }

    @DeleteMapping("/users/{id}/rates")
    public HttpEntity<RatesDTO> cancelRating(@PathVariable("id") long id,
                                             @RequestParam("type") String type,
                                             @RequestParam("item") long item) {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findById(id);
        if(user==null) {
            throw new InvalidIdException();
        }
        if (!userRepository
                .findByUsername(username)
                .equals(user)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (type.equalsIgnoreCase("film")) {
            if (!user.getRatedFilms().containsKey(item)) {
                throw new InvalidParamException("You haven't rated this film!");
            }
            Film film = filmRepository.findById(item);
            film.unrate(user
                    .getRatedFilms()
                    .get(item));
            filmRepository.save(film);
            user.getRatedFilms()
                    .remove(item);
        } else if(type.equalsIgnoreCase("actor")) {
            if (!user.getRatedActors().containsKey(item)) {
                throw new InvalidParamException("You haven't rated this actor!");
            }
            Actor actor = actorRepository.findById(item);
            actor.unrate(user
                    .getRatedActors()
                    .get(item));
            actorRepository.save(actor);
            user.getRatedActors()
                    .remove(item);
        } else if(type.equalsIgnoreCase("director")) {
            if (!user.getRatedDirectors().containsKey(item)) {
                throw new InvalidParamException("You haven't rated this director!");
            }
            Director director = directorRepository.findById(item);
            director.unrate(user
                    .getRatedDirectors()
                    .get(item));
            directorRepository.save(director);
            user.getRatedDirectors()
                    .remove(item);
        } else {
            throw new InvalidParamException("Invalid argument!");
        }
        userRepository.save(user);
        return ResponseEntity.ok(createRatesToDisplay(user));
    }

    private RatesDTO createRatesToDisplay(User user) {
        List<MapActorItemDTO> actorDTOList = new ArrayList<>();
        List<MapDirectorItemDTO> directorDTOList = new ArrayList<>();
        List<MapFilmItemDTO> filmDTOList = new ArrayList<>();

        for (Long key : user.getRatedFilms().keySet()) {
            filmDTOList.add(new MapFilmItemDTO(
                    filmRepository.findById(key),
                    user.getRatedFilms().get(key)
            ));
        }

        for (Long key : user.getRatedActors().keySet()) {
            actorDTOList.add(new MapActorItemDTO(
                    actorRepository.findById(key),
                    user.getRatedActors().get(key)
            ));
        }

        for (Long key : user.getRatedDirectors().keySet()) {
            directorDTOList.add(new MapDirectorItemDTO(
                    directorRepository.findById(key),
                    user.getRatedDirectors().get(key)
            ));
        }
        return new RatesDTO(actorDTOList, directorDTOList, filmDTOList);
    }
}
