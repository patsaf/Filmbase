package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDTO {

    private long id;
    private String username;

    private LocalDate registerDate;
    private boolean isAdmin;

    List<FilmBriefDTO> favFilms;
    List<ActorDTO> favActors;
    List<DirectorDTO> favDirectors;

    List<FilmBriefDTO> filmWishlist;

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        registerDate = user.getRegisterDate();
        isAdmin = user.isAdmin();
        initCollections();
        user.getFavFilms()
                .stream()
                .forEach(film -> favFilms.add(new FilmBriefDTO(film)));
        user.getFavActors()
                .stream()
                .forEach(actor -> favActors.add(new ActorDTO(actor)));
        user.getFavDirectors()
                .stream()
                .forEach(director -> favDirectors.add(new DirectorDTO(director)));
        user.getFilmWishlist()
                .stream()
                .forEach(film -> filmWishlist.add(new FilmBriefDTO(film)));
    }

    private void initCollections() {
        favFilms = new ArrayList<>();
        favActors = new ArrayList<>();
        favDirectors = new ArrayList<>();
        filmWishlist = new ArrayList<>();
    }

    public boolean checkIfDataEquals(UserDTO userDTO) {
        return (username.equals(userDTO.getUsername()) &&
                registerDate.equals(userDTO.getRegisterDate()));
    }
}
