package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.WishlistDTO;
import com.example.patrycja.filmbase.exception.InvalidIdException;
import com.example.patrycja.filmbase.exception.InvalidParamException;
import com.example.patrycja.filmbase.model.User;
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
public class UserWishlistController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FilmRepository filmRepository;

    @GetMapping("/users/{id}/wishlist")
    @ResponseStatus(HttpStatus.OK)
    public WishlistDTO findWishlist(@PathVariable("id") long id) {
        User user = userRepository.findById(id);

        if(user==null) {
            throw new InvalidIdException();
        }

        return new WishlistDTO(user);
    }

    @PostMapping("/users/{id}/wishlist")
    public HttpEntity<WishlistDTO> moveToFavourites(@PathVariable("id") long id,
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
            if (user.getFilmWishlist()
                    .stream()
                    .anyMatch(film -> film.getId() == itemid)) {
                user.getFilmWishlist().remove(filmRepository.findById(itemid));
                user.getFavFilms().add(filmRepository.findById(itemid));
            } else {
                throw new InvalidParamException("This film isn't even on your list!");
            }
            userRepository.save(user);
            return ResponseEntity.ok(new WishlistDTO(user));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/users/{id}/wishlist")
    public HttpEntity<WishlistDTO> deleteWish(@PathVariable("id") long id,
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
            if (user.getFilmWishlist()
                    .stream()
                    .anyMatch(film -> film.getId() == itemid)) {
                user.getFilmWishlist().remove(filmRepository.findById(itemid));
            } else {
                throw new InvalidParamException("This film isn't even on your list!");
            }
            userRepository.save(user);
            return ResponseEntity.ok(new WishlistDTO(user));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
