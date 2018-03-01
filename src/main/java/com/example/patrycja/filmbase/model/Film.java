package com.example.patrycja.filmbase.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
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

    private int productionYear;

    public Film() {}

    public Film(String title, Director director, List<String> types, int productionYear) {
        this.title = title;
        this.director = director;
        this.types = types;
        this.productionYear = productionYear;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setType(List<String> types) {
        this.types = types;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public Boolean checkIfContentEquals(Film film) {
        if(title.equals(film.getTitle()) &&
                director.checkIfDataEquals(film.getDirector()) &&
                types.equals(film.getTypes()) &&
                productionYear == film.getProductionYear()) {
            return true;
        }
        return false;
    }
}
