package com.example.patrycja.filmbase.DTO.maps;

import com.example.patrycja.filmbase.DTO.DirectorDTO;
import com.example.patrycja.filmbase.model.Director;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MapDirectorItemDTO {

    private DirectorDTO director;
    private double rate;

    public MapDirectorItemDTO(Director director, double rate) {
        this.director = new DirectorDTO(director);
        this.rate = rate;
    }
}
