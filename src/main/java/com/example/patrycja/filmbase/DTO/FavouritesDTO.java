package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class FavouritesDTO {

    private List<FilmBriefDTO> films;
    private List<ActorDTO> actors;
    private List<DirectorDTO> directors;

    public FavouritesDTO(User user) {
        films = new ArrayList<>();
        actors = new ArrayList<>();
        directors = new ArrayList<>();
        user.getFavFilms()
                .forEach(film -> films.add(new FilmBriefDTO(film)));
        user.getFavActors()
                .forEach(actor -> actors.add(new ActorDTO(actor)));
        user.getFavDirectors()
                .forEach(director -> directors.add(new DirectorDTO(director)));
    }
}
