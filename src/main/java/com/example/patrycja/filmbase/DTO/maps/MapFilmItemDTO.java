package com.example.patrycja.filmbase.DTO.maps;

import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.example.patrycja.filmbase.model.Film;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MapFilmItemDTO {

    private FilmBriefDTO film;
    private double rate;

    public MapFilmItemDTO(Film film, double rate) {
        this.film = new FilmBriefDTO(film);
        this.rate = rate;
    }
}
