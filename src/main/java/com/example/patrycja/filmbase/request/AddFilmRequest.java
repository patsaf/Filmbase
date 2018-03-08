package com.example.patrycja.filmbase.request;

import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.model.Film;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class AddFilmRequest {

    @NotNull
    private String title;

    @NotNull
    private List<String> types;

    @NotNull
    @Min(1900)
    private int productionYear;

    @NotNull
    private String directorFirstName;

    @NotNull
    private String directorLastName;

    private List<AddActorRequest> actorRequests;

    public List<AddActorRequest> getActorRequests() {
        return actorRequests;
    }

    public void setActorRequests(List<AddActorRequest> actorRequests) {
        this.actorRequests = actorRequests;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getDirectorFirstName() {
        return directorFirstName;
    }

    public void setDirectorFirstName(String directorFirstName) {
        this.directorFirstName = directorFirstName;
    }

    public String getDirectorLastName() {
        return directorLastName;
    }

    public void setDirectorLastName(String directorLastName) {
        this.directorLastName = directorLastName;
    }

    public AddFilmRequest() {
    }

    public AddFilmRequest(String title, List<String> types, int productionYear,
                          String directorFirstName, String directorLastName,
                          List<AddActorRequest> actorRequests) {
        this.title = title;
        this.types = types;
        this.productionYear = productionYear;
        this.directorFirstName = directorFirstName;
        this.directorLastName = directorLastName;
        this.actorRequests = actorRequests;
    }

    public String getTitle() {
        return title;
    }

    public Film getFilm() {
        List<Actor> cast = new ArrayList<>();
        for (AddActorRequest actorRequest : actorRequests) {
            cast.add(new Actor(actorRequest.getFirstName(), actorRequest.getLastName()));
        }
        return new Film(title, new Director(directorFirstName, directorLastName), types, productionYear, cast);
    }

    public FilmDTO getDTO() {
        return new FilmDTO(getFilm());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTypes() {
        return types;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }
}
