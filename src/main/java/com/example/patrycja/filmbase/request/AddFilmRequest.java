package com.example.patrycja.filmbase.request;

import com.example.patrycja.filmbase.model.Film;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class AddFilmRequest {

    @NotNull
    private String title;

    @NotNull
    private String director;

    @NotNull
    private List<String> types;

    @NotNull
    @Min(1900)
    private int productionYear;

    public AddFilmRequest() {}

    public AddFilmRequest(String title, String director, List<String> types, int productionYear) {
        this.title = title;
        this.director = director;
        this.types = types;
        this.productionYear = productionYear;
    }

    public String getTitle() {
        return title;
    }

    public Film getFilm() {
        return new Film(title, director, types, productionYear);
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
}
