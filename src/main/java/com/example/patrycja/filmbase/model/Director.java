package com.example.patrycja.filmbase.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "director")
    List<Film> films;

    public Director() {
    }

    public Director(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        films = new ArrayList<>();
    }

    public Director(String firstName, String lastName, LocalDate dateOfBirth, List<Film> films) {
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

    public Boolean checkIfDataEquals(Director director) {
        if (firstName.equals(director.getFirstName()) &&
                lastName.equals(director.getLastName()) &&
                dateOfBirth == director.getDateOfBirth()) {
            return true;
        }
        return false;
    }
}
