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

    public Director(DirectorBuilder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.dateOfBirth = builder.dateOfBirth;
        this.films = builder.films;
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

    public static class DirectorBuilder {
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private List<Film> films;

        public DirectorBuilder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public DirectorBuilder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public DirectorBuilder films(List<Film> films) {
            this.films = films;
            return this;
        }

        public Director build() {
            return new Director(this);
        }
    }
}
