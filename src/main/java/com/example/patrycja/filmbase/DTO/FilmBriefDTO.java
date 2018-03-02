package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.Film;

public class FilmBriefDTO {

    private long id;
    private String title;
    private int productionYear;

    public FilmBriefDTO() {}

    public FilmBriefDTO(Film film) {
        this.id = film.getId();
        this.title = film.getTitle();
        this.productionYear = film.getProductionYear();
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

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public Boolean checkIfContentEquals(FilmBriefDTO briefDTO) {
        if(title.equals(briefDTO.getTitle()) &&
                productionYear == briefDTO.getProductionYear()) {
            return true;
        }
        return false;
    }
}
