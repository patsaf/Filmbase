package com.example.patrycja.filmbase.model;

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

    private double rate;
    private double sum;
    private long count;

    public Director() {
    }

    public Director(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        films = new ArrayList<>();
        rate = 0;
        sum = count = 0;
    }

    public Director(String firstName, String lastName, LocalDate dateOfBirth, List<Film> films) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.films = films;
        rate = 0;
        sum = count = 0;
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

    public double getRate() {
        return rate;
    }

    public double getSum() {
        return sum;
    }

    public long getCount() {
        return count;
    }

    public void rate(double i) {
        sum += i;
        count++;
        rate = sum/count;
    }

    public void unrate(double i) {
        sum = (rate*count) - i;
        count--;
        if(count == 0) {
            rate = 0;
        } else {
            rate = sum / count;
        }
    }

    public Boolean checkIfDataEquals(Director director) {
        return firstName.equals(director.getFirstName()) &&
                lastName.equals(director.getLastName()) &&
                dateOfBirth == director.getDateOfBirth() &&
                rate == director.getRate();
    }
}
