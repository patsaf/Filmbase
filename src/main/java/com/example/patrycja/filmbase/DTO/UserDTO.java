package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private long id;
    private String username;

    private LocalDate registerDate;
    private boolean isAdmin;

    List<FilmBriefDTO> favFilms;
    List<ActorDTO> favActors;
    List<DirectorDTO> favDirectors;

    List<FilmBriefDTO> filmWishlist;

    public UserDTO() {
    }

    public UserDTO(long id, String username, LocalDate registerDate, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.registerDate = registerDate;
        this.isAdmin = isAdmin;
        initCollections();
    }

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        registerDate = user.getRegisterDate();
        isAdmin = user.isAdmin();
        initCollections();
        user.getFavFilms()
                .stream()
                .forEach(film -> favFilms.add(new FilmBriefDTO(film)));
        user.getFavActors()
                .stream()
                .forEach(actor -> favActors.add(new ActorDTO(actor)));
        user.getFavDirectors()
                .stream()
                .forEach(director -> favDirectors.add(new DirectorDTO(director)));
        user.getFilmWishlist()
                .stream()
                .forEach(film -> filmWishlist.add(new FilmBriefDTO(film)));
    }

    private void initCollections() {
        favFilms = new ArrayList<>();
        favActors = new ArrayList<>();
        favDirectors = new ArrayList<>();
        filmWishlist = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public List<FilmBriefDTO> getFavFilms() {
        return favFilms;
    }

    public void setFavFilms(List<FilmBriefDTO> favFilms) {
        this.favFilms = favFilms;
    }

    public List<ActorDTO> getFavActors() {
        return favActors;
    }

    public void setFavActors(List<ActorDTO> favActors) {
        this.favActors = favActors;
    }

    public List<DirectorDTO> getFavDirectors() {
        return favDirectors;
    }

    public void setFavDirectors(List<DirectorDTO> favDirectors) {
        this.favDirectors = favDirectors;
    }

    public List<FilmBriefDTO> getFilmWishlist() {
        return filmWishlist;
    }

    public void setFilmWishlist(List<FilmBriefDTO> filmWishlist) {
        this.filmWishlist = filmWishlist;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean checkIfDataEquals(UserDTO userDTO) {
        return (username.equals(userDTO.getUsername()) &&
                registerDate.equals(userDTO.getRegisterDate()));
    }
}
