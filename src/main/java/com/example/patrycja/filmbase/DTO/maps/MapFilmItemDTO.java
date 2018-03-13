package com.example.patrycja.filmbase.DTO.maps;

import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.example.patrycja.filmbase.model.Film;

public class MapFilmItemDTO {

    private FilmBriefDTO film;
    private double rate;

    public MapFilmItemDTO() {
    }

    public MapFilmItemDTO(Film film, double rate) {
        this.film = new FilmBriefDTO(film);
        this.rate = rate;
    }

    public FilmBriefDTO getFilm() {
        return film;
    }

    public void setFilm(FilmBriefDTO film) {
        this.film = film;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
