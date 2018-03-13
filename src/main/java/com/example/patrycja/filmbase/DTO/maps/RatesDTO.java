package com.example.patrycja.filmbase.DTO.maps;

import java.util.List;

public class RatesDTO {

    List<MapActorItemDTO> actorList;
    List<MapDirectorItemDTO> directorList;
    List<MapFilmItemDTO> filmList;

    public RatesDTO() {}

    public RatesDTO(List<MapActorItemDTO> actorList,
                    List<MapDirectorItemDTO> directorList,
                    List<MapFilmItemDTO> filmList) {
        this.actorList = actorList;
        this.directorList = directorList;
        this.filmList = filmList;
    }

    public List<MapActorItemDTO> getActorList() {
        return actorList;
    }

    public void setActorList(List<MapActorItemDTO> actorList) {
        this.actorList = actorList;
    }

    public List<MapDirectorItemDTO> getDirectorList() {
        return directorList;
    }

    public void setDirectorList(List<MapDirectorItemDTO> directorList) {
        this.directorList = directorList;
    }

    public List<MapFilmItemDTO> getFilmList() {
        return filmList;
    }

    public void setFilmList(List<MapFilmItemDTO> filmList) {
        this.filmList = filmList;
    }
}
