package com.example.patrycja.filmbase.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    @ManyToMany(mappedBy = "cast")
    private List<Film> films;

    public Actor() {
    }

    public Actor(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        films = new ArrayList<>();
    }

    public Actor(String firstName, String lastName, LocalDate dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        films = new ArrayList<>();
    }

    public Actor(String firstName, String lastName, LocalDate dateOfBirth, List<Film> films) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.films = films;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public List<Film> getFilms() {
        return films;
    }

    public Boolean checkIfDataEquals(Actor actor) {
        if(firstName.equals(actor.getFirstName()) &&
                lastName.equals(actor.getLastName()) &&
                dateOfBirth == actor.getDateOfBirth() &&
                films.equals(actor.getFilms())) {
            return true;
        }
        return false;
    }
}
