package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.User;

import java.util.ArrayList;
import java.util.List;

public class WishlistDTO {

    private List<FilmBriefDTO> films;

    public WishlistDTO() {
    }

    public WishlistDTO(User user) {
        films = new ArrayList<>();
        user.getFilmWishlist()
                .forEach(film -> films.add(new FilmBriefDTO(film)));
    }

    public List<FilmBriefDTO> getFilms() {
        return films;
    }

    public void setFilms(List<FilmBriefDTO> films) {
        this.films = films;
    }
}
