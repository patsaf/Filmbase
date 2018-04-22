package com.example.patrycja.filmbase.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    @ManyToMany(mappedBy = "cast")
    private List<Film> films;

    private double rate;
    private double sum;
    private long count;

    public Actor(ActorBuilder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.dateOfBirth = builder.dateOfBirth;
        this.films = builder.films;
        rate = 0;
        sum = count = 0;
    }

    public void rate(double i) {
        sum += i;
        count++;
        rate = sum / count;
    }

    public void unrate(double i) {
        sum = (rate * count) - i;
        count--;
        if (count == 0) {
            rate = 0;
        } else {
            rate = sum / count;
        }
    }

    public boolean hasDateOfBirth() {
        return !(dateOfBirth==null);
    }

    public boolean hasAnyFilms() {
        return !(films==null);
    }

    public static class ActorBuilder {
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private List<Film> films;

        public ActorBuilder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public ActorBuilder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public ActorBuilder films(List<Film> films) {
            this.films = films;
            return this;
        }

        public Actor build() {
            return new Actor(this);
        }
    }
}
