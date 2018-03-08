package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActorDTO {

    private long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private List<FilmBriefDTO> films;

    public ActorDTO() {
    }

    public ActorDTO(long id, String firstName, String lastName, LocalDate dateOfBirth, List<FilmBriefDTO> films) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.films = films;
    }

    public ActorDTO(Actor actor) {
        this.id = actor.getId();
        this.firstName = actor.getFirstName();
        this.lastName = actor.getLastName();
        this.dateOfBirth = actor.getDateOfBirth();
        this.films = new ArrayList<>();
        actor.getFilms()
                .forEach(film -> films.add(new FilmBriefDTO(film)));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<FilmBriefDTO> getFilms() {
        return films;
    }

    public void setFilms(List<FilmBriefDTO> films) {
        this.films = films;
    }

    public Boolean checkIfDataEquals(ActorDTO actorDTO) {
        if(firstName.equals(actorDTO.getFirstName()) &&
                lastName.equals(actorDTO.getLastName()) &&
                dateOfBirth == actorDTO.getDateOfBirth() &&
                films.equals(actorDTO.getFilms())) {
            return true;
        }
        return false;
    }
}
