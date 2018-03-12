package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.User;

import java.util.ArrayList;
import java.util.List;

public class FavouritesDTO {

    private List<FilmBriefDTO> films;
    private List<ActorDTO> actors;
    private List<DirectorDTO> directors;

    public FavouritesDTO() {
    }

    public FavouritesDTO(User user) {
        films = new ArrayList<>();
        actors = new ArrayList<>();
        directors = new ArrayList<>();
        user.getFavFilms()
                .forEach(film -> films.add(new FilmBriefDTO(film)));
        user.getFavActors()
                .forEach(actor -> actors.add(new ActorDTO(actor)));
        user.getFavDirectors()
                .forEach(director -> directors.add(new DirectorDTO(director)));
    }

    public List<FilmBriefDTO> getFilms() {
        return films;
    }

    public void setFilms(List<FilmBriefDTO> films) {
        this.films = films;
    }

    public List<ActorDTO> getActors() {
        return actors;
    }

    public void setActors(List<ActorDTO> actors) {
        this.actors = actors;
    }

    public List<DirectorDTO> getDirectors() {
        return directors;
    }

    public void setDirectors(List<DirectorDTO> directors) {
        this.directors = directors;
    }
}
