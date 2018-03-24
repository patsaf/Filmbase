package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class WishlistDTO {

    private List<FilmBriefDTO> films;

    public WishlistDTO(User user) {
        films = new ArrayList<>();
        user.getFilmWishlist()
                .forEach(film -> films.add(new FilmBriefDTO(film)));
    }
}
