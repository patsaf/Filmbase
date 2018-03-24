package com.example.patrycja.filmbase.DTO.maps;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatesDTO {

    List<MapActorItemDTO> actorList;
    List<MapDirectorItemDTO> directorList;
    List<MapFilmItemDTO> filmList;
}
