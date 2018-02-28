package com.example.patrycja.filmbase.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;
    private String director;

    @ElementCollection
    private List<String> types;

    private int productionYear;

    public Film() {}

    public Film(String title, String director, List<String> types, int productionYear) {
        this.title = title;
        this.director = director;
        this.types = types;
        this.productionYear = productionYear;
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

    public void setType(List<String> types) {
        this.types = types;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public Boolean checkIfContentEquals(Film film) {
        if(title.equals(film.getTitle()) &&
                director.equals(film.getDirector()) &&
                types.equals(film.getTypes()) &&
                productionYear == film.getProductionYear()) {
            return true;
        }
        return false;
    }
}
