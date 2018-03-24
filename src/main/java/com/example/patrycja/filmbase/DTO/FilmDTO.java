package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.Film;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class FilmDTO {

    private long id;
    private String title;
    private String director;
    private List<String> types;
    private int productionYear;
    private List<String> cast;
    private double rate;
    private long count;

    public FilmDTO(Film film) {
        this.id = film.getId();
        this.title = film.getTitle();
        this.director = film.getDirector().getFirstName() + " " + film.getDirector().getLastName();
        this.types = film.getTypes();
        this.productionYear = film.getProductionYear();
        cast = new ArrayList<>();
        film.getCast()
                .forEach(actor -> cast.add(actor.getFirstName() + " " + actor.getLastName()));
        this.rate = film.getRate();
        this.count = film.getCount();
    }

    public Boolean checkIfContentEquals(FilmDTO filmDTO) {
        return title.equals(filmDTO.getTitle()) &&
                director.equals(filmDTO.getDirector()) &&
                types.equals(filmDTO.getTypes()) &&
                cast.equals(filmDTO.getCast()) &&
                productionYear == filmDTO.getProductionYear() &&
                rate == filmDTO.getRate() &&
                count == filmDTO.getCount();
    }
}