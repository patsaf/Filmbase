package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.Film;

public class FilmBriefDTO {

    private long id;
    private String title;
    private int productionYear;
    private double rate;

    public FilmBriefDTO() {
    }

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

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Boolean checkIfContentEquals(FilmBriefDTO briefDTO) {
        return title.equals(briefDTO.getTitle()) &&
                productionYear == briefDTO.getProductionYear() &&
                rate == briefDTO.getRate();
    }
}
