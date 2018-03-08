package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Film;

import java.util.ArrayList;
import java.util.List;

public class FilmDTO {

    private long id;
    private String title;
    private String director;
    private List<String> types;
    private int productionYear;
    private List<String> cast;

    public FilmDTO() {
    }

    public FilmDTO(Film film) {
        this.id = film.getId();
        this.title = film.getTitle();
        this.director = film.getDirector().getFirstName() + " " + film.getDirector().getLastName();
        this.types = film.getTypes();
        this.productionYear = film.getProductionYear();
        cast = new ArrayList<>();
        film.getCast()
                .forEach(actor -> cast.add(actor.getFirstName() + " " + actor.getLastName()));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public List<String> getCast() {
        return cast;
    }

    public void setCast(List<String> cast) {
        this.cast = cast;
    }

    public Boolean checkIfContentEquals(FilmDTO filmDTO) {
        if (title.equals(filmDTO.getTitle()) &&
                director.equals(filmDTO.getDirector()) &&
                types.equals(filmDTO.getTypes()) &&
                cast.equals(filmDTO.getCast()) &&
                productionYear == filmDTO.getProductionYear()) {
            return true;
        }
        return false;
    }
}