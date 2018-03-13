package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.FavouritesDTO;
import com.example.patrycja.filmbase.DTO.UserDTO;
import com.example.patrycja.filmbase.DTO.WishlistDTO;
import com.example.patrycja.filmbase.DTO.maps.MapActorItemDTO;
import com.example.patrycja.filmbase.DTO.maps.MapDirectorItemDTO;
import com.example.patrycja.filmbase.DTO.maps.MapFilmItemDTO;
import com.example.patrycja.filmbase.DTO.maps.RatesDTO;
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
        String username = ((UserDetails) principal).getUsername();
        if (userRepository
                .findByUsername(username)
                .isAdmin()) {
            User user = userRepository.findById(id);
            if (user == null) {
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

    @GetMapping("/users/{id}/rates")
    public RatesDTO findAllUserRatings(@PathVariable("id") long id) {
        User user = userRepository.findById(id);

        /*List<MapActorItemDTO> actorDTOList = new ArrayList<>();
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
        }*/
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
