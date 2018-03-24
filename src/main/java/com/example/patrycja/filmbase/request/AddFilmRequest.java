package com.example.patrycja.filmbase.request;

import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.model.Film;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
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

    public AddFilmRequest(AddFilmRequestBuilder builder) {
        this.title = builder.title;
        this.types = builder.types;
        this.productionYear = builder.productionYear;
        this.directorFirstName = builder.directorFirstName;
        this.directorLastName = builder.directorLastName;
        this.actorRequests = builder.actorRequests;
    }

    public Film getFilm() {
        List<Actor> cast = new ArrayList<>();
        for (AddActorRequest actorRequest : actorRequests) {
            cast.add(new Actor.ActorBuilder(actorRequest.getFirstName(), actorRequest.getLastName())
                    .build());
        }
        return new Film.FilmBuilder(title)
                .director(new Director.DirectorBuilder(directorFirstName, directorLastName)
                        .build())
                .types(types)
                .productionYear(productionYear)
                .cast(cast)
                .build();
    }

    public FilmDTO getDTO() {
        return new FilmDTO(getFilm());
    }

    public static class AddFilmRequestBuilder {
        private String title;
        private List<String> types;
        private int productionYear;
        private String directorFirstName;
        private String directorLastName;
        private List<AddActorRequest> actorRequests;

        public AddFilmRequestBuilder(String title) {
            this.title = title;
        }

        public AddFilmRequestBuilder types(List<String> types) {
            this.types = types;
            return this;
        }

        public AddFilmRequestBuilder productionYear(int productionYear) {
            this.productionYear = productionYear;
            return this;
        }

        public AddFilmRequestBuilder director(String directorFirstName, String directorLastName) {
            this.directorFirstName = directorFirstName;
            this.directorLastName = directorLastName;
            return this;
        }

        public AddFilmRequestBuilder actorRequests(List<AddActorRequest> actorRequests) {
            this.actorRequests = actorRequests;
            return this;
        }

        public AddFilmRequest build() {
            return new AddFilmRequest(this);
        }
    }
}
