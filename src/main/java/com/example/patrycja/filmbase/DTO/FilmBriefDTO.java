package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.Film;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilmBriefDTO {

    private long id;
    private String title;
    private int productionYear;
    private double rate;

    public FilmBriefDTO(FilmDTO film) {
        this.id = film.getId();
        this.title = film.getTitle();
        this.productionYear = film.getProductionYear();
        this.rate = film.getRate();
    }

    public FilmBriefDTO(Film film) {
        this.id = film.getId();
        this.title = film.getTitle();
        this.productionYear = film.getProductionYear();
        this.rate = film.getRate();
    }

    public Boolean checkIfContentEquals(FilmBriefDTO briefDTO) {
        return title.equals(briefDTO.getTitle()) &&
                productionYear == briefDTO.getProductionYear() &&
                rate == briefDTO.getRate();
    }
}
