package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.FavouritesDTO;
import com.example.patrycja.filmbase.exception.InvalidIdException;
import com.example.patrycja.filmbase.exception.InvalidParamException;
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

@RestController
public class UserFavouritesController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    DirectorRepository directorRepository;

    @GetMapping("/users/{id}/favourites")
    @ResponseStatus(HttpStatus.OK)
    public FavouritesDTO findFavourites(@PathVariable("id") long id) {
        User user = userRepository.findById(id);

        if(user==null) {
            throw new InvalidIdException();
        }

        return new FavouritesDTO(user);
    }

    @DeleteMapping("/users/{id}/favourites")
    public HttpEntity<FavouritesDTO> deleteFavourite(@PathVariable("id") long id,
                                                     @RequestParam("action") String action,
                                                     @RequestParam("item") long itemid) {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findById(id);

        if(user==null) {
            throw new InvalidIdException();
        }

        if ((userRepository
                .findByUsername(username)).equals(user)) {
            if (action.equalsIgnoreCase("film")) {

                if (user.getFavFilms()
                        .stream()
                        .anyMatch(film -> film.getId() == itemid)) {
                    user.getFavFilms().remove(filmRepository.findById(itemid));
                } else {
                    throw new InvalidParamException("This film isn't even on your list!");
                }

            } else if (action.equalsIgnoreCase("actor")) {

                if (user.getFavActors()
                        .stream()
                        .anyMatch(actor -> actor.getId() == itemid)) {
                    user.getFavActors().remove(actorRepository.findById(itemid));
                } else {
                    throw new InvalidParamException("This actor isn't even on your list!");
                }

            } else if (action.equalsIgnoreCase("director")) {

                if (user.getFavDirectors()
                        .stream()
                        .anyMatch(director -> director.getId() == itemid)) {
                    user.getFavDirectors().remove(directorRepository.findById(itemid));
                } else {
                    throw new InvalidParamException("This director isn't even on your list!");
                }

            } else {
                throw new InvalidParamException("Invalid argument!");
            }
            userRepository.save(user);
            return ResponseEntity.ok(new FavouritesDTO(user));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
