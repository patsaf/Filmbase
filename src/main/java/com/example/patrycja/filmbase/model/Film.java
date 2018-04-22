package com.example.patrycja.filmbase.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "id_director")
    private Director director;

    @ElementCollection
    private List<String> types;

    @ManyToMany
    private List<Actor> cast;

    private int productionYear;

    private double rate;
    private double sum;
    private long count;

    public Film(FilmBuilder builder) {
        this.title = builder.title;
        this.director = builder.director;
        this.types = builder.types;
        this.productionYear = builder.productionYear;
        this.cast = builder.cast;
        rate = 0;
        sum = count = 0;
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

    public Boolean checkIfContentEquals(Film film) {
        return title.equals(film.getTitle()) &&
                director.checkIfDataEquals(film.getDirector()) &&
                types.equals(film.getTypes()) &&
                productionYear == film.getProductionYear();
    }

    public void addActorToCast(Actor actor) {
        cast.add(actor);
    }

    public static class FilmBuilder {
        private String title;
        private Director director;
        private List<String> types;
        private List<Actor> cast;
        private int productionYear;

        public FilmBuilder(String title) {
            this.title = title;
        }

        public FilmBuilder director(Director director) {
            this.director = director;
            return this;
        }

        public FilmBuilder types(List<String> types) {
            this.types = types;
            return this;
        }

        public FilmBuilder cast(List<Actor> cast) {
            this.cast = cast;
            return this;
        }

        public FilmBuilder productionYear(int productionYear){
            this.productionYear = productionYear;
            return this;
        }

        public Film build() {
            return new Film(this);
        }
    }
}
