package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.Actor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ActorDTO {

    private long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private List<FilmBriefDTO> films;
    private double rate;
    private long count;

    public ActorDTO(long id, String firstName, String lastName, LocalDate dateOfBirth,
                    List<FilmBriefDTO> films, double rate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.films = films;
        this.rate = rate;
        count = 0;
    }

    public ActorDTO(Actor actor) {
        this.id = actor.getId();
        this.firstName = actor.getFirstName();
        this.lastName = actor.getLastName();
        this.dateOfBirth = actor.getDateOfBirth();
        this.films = new ArrayList<>();
        actor.getFilms()
                .forEach(film -> films.add(new FilmBriefDTO(film)));
        this.rate = actor.getRate();
        this.count = actor.getCount();
    }

    public Boolean checkIfDataEquals(ActorDTO actorDTO) {
        return firstName.equals(actorDTO.getFirstName()) &&
                lastName.equals(actorDTO.getLastName()) &&
                dateOfBirth == actorDTO.getDateOfBirth() &&
                rate == actorDTO.getRate() &&
                count == actorDTO.getCount();
    }
}
