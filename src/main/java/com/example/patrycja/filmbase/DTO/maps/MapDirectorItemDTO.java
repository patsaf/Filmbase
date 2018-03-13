package com.example.patrycja.filmbase.DTO.maps;

import com.example.patrycja.filmbase.DTO.DirectorDTO;
import com.example.patrycja.filmbase.model.Director;

public class MapDirectorItemDTO {

    private DirectorDTO director;
    private double rate;

    public MapDirectorItemDTO() {
    }

    public MapDirectorItemDTO(Director director, double rate) {
        this.director = new DirectorDTO(director);
        this.rate = rate;
    }

    public DirectorDTO getDirector() {
        return director;
    }

    public void setDirector(DirectorDTO director) {
        this.director = director;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
