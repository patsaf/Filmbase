package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.FavouritesDTO;
import com.example.patrycja.filmbase.DTO.UserDTO;
import com.example.patrycja.filmbase.DTO.WishlistDTO;
import com.example.patrycja.filmbase.exception.DuplicateException;
import com.example.patrycja.filmbase.exception.InvalidParamException;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.model.User;
import com.example.patrycja.filmbase.repository.ActorRepository;
import com.example.patrycja.filmbase.repository.DirectorRepository;
import com.example.patrycja.filmbase.repository.FilmRepository;
import com.example.patrycja.filmbase.repository.UserRepository;
import com.example.patrycja.filmbase.request.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.sun.jmx.snmp.ThreadContext.contains;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    DirectorRepository directorRepository;

    @PostConstruct
    public void introduceAdmin() {
        User admin = new User("admin", "hardpass", "admin@gmail.com", LocalDate.now());
        admin.makeAdmin();
        userRepository.save(admin);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()) != null) {
            throw new DuplicateException("User with given email already exists!");
        } else if (userRepository.findByUsername(signUpRequest.getUsername()) != null) {
            throw new DuplicateException("User with given username already exists!");
        }
        userRepository.save(signUpRequest.getUser());
    }

    @GetMapping("/users")
    public List<UserDTO> findAllUsers() {
        List<UserDTO> allDTOs = new ArrayList<>();
        userRepository.findAll()
                .forEach(user -> allDTOs.add(new UserDTO(user)));
        return allDTOs;
    }

    @GetMapping("/users/{id}")
    public HttpEntity<UserDTO> findUser(@PathVariable("id") long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            return ResponseEntity.ok(new UserDTO(user));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/users/{id}")
    public HttpEntity<UserDTO> upgradeToAdmin(@PathVariable("id") long id) {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        if(userRepository
                .findByUsername(username)
                .isAdmin()) {
            User user = userRepository.findById(id);
            if(user==null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            user.makeAdmin();
            userRepository.save(user);
            return ResponseEntity.ok(new UserDTO(user));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/users/{id}/favourites")
    @ResponseStatus(HttpStatus.OK)
    public FavouritesDTO findFavourites(@PathVariable("id") long id) {
        User user = userRepository.findById(id);
        return new FavouritesDTO(user);
    }

    @GetMapping("/users/{id}/wishlist")
    @ResponseStatus(HttpStatus.OK)
    public WishlistDTO findWishlist(@PathVariable("id") long id) {
        User user = userRepository.findById(id);
        return new WishlistDTO(user);
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
        if ((userRepository
                .findByUsername(username)).equals(user)) {
            if(action.equalsIgnoreCase("film")) {

                if(user.getFavFilms()
                        .stream()
                        .anyMatch(film -> film.getId()==itemid)) {
                    user.getFavFilms().remove(filmRepository.findById(itemid));
                } else {
                    throw new InvalidParamException("This film isn't even on your list!");
                }

            } else if(action.equalsIgnoreCase("actor")) {

                if(user.getFavActors()
                        .stream()
                        .anyMatch(actor -> actor.getId()==itemid)) {
                    user.getFavActors().remove(actorRepository.findById(itemid));
                } else {
                    throw new InvalidParamException("This actor isn't even on your list!");
                }

            } else if(action.equalsIgnoreCase("director")) {

                if(user.getFavDirectors()
                        .stream()
                        .anyMatch(director -> director.getId()==itemid)) {
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

    @DeleteMapping("/users/{id}/wishlist")
    public HttpEntity<WishlistDTO> deleteWish(@PathVariable("id") long id,
                                              @RequestParam("item") long itemid) {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findById(id);
        if ((userRepository
                .findByUsername(username)).equals(user)) {
                if(user.getFilmWishlist()
                        .stream()
                        .anyMatch(film -> film.getId()==itemid)) {
                    user.getFilmWishlist().remove(filmRepository.findById(itemid));
                } else {
                    throw new InvalidParamException("This film isn't even on your list!");
                }
            userRepository.save(user);
            return ResponseEntity.ok(new WishlistDTO(user));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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
        if ((userRepository
                .findByUsername(username)).equals(user)) {
            if(user.getFilmWishlist()
                    .stream()
                    .anyMatch(film -> film.getId()==itemid)) {
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
}
