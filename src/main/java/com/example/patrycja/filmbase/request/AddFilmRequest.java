package com.example.patrycja.filmbase.request;

import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.model.Film;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class AddFilmRequest {

    @NotNull
    private String title;

    @NotNull
    private List<String> types;

    @NotNull
    @Min(1900)
    private int productionYear;

    @NotNull
    private String directorFirstName;

    @NotNull
    private String directorLastName;

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getDirectorFirstName() {
        return directorFirstName;
    }

    public void setDirectorFirstName(String directorFirstName) {
        this.directorFirstName = directorFirstName;
    }

    public String getDirectorLastName() {
        return directorLastName;
    }

    public void setDirectorLastName(String directorLastName) {
        this.directorLastName = directorLastName;
    }

    public AddFilmRequest() {}

    public AddFilmRequest(String title, List<String> types, int productionYear,
                          String directorFirstName, String directorLastName) {
        this.title = title;
        this.types = types;
        this.productionYear = productionYear;
        this.directorFirstName = directorFirstName;
        this.directorLastName = directorLastName;
    }

    public String getTitle() {
        return title;
    }

    public Film getFilm() {
        return new Film(title, new Director(directorFirstName, directorLastName), types, productionYear);
    }

    public FilmDTO getDTO() {
        return new FilmDTO(getFilm());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTypes() {
        return types;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }
}
