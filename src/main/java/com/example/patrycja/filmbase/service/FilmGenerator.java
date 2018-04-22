package com.example.patrycja.filmbase.service;

import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.model.Film;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FilmGenerator {

    private final String filePath = "Films";
    private final FileLinesReader fileLinesReader;
    private List<Film> films;
    private long count;

    public FilmGenerator() {
        films = new ArrayList<>();
        fileLinesReader = new FileLinesReader(filePath);

        for (String lineDivider : fileLinesReader.getLines()) {
            String[] content = lineDivider
                    .split("; ");
            List<String> typeList = Arrays
                    .asList(
                            content[3]
                                    .split(", "));

            String[] fullNames = content[5].split(", ");
            List<Actor> cast = new ArrayList<>();
            for (String name : fullNames) {
                String[] separatedNames = name.split("_");
                cast.add(new Actor.ActorBuilder(separatedNames[0], separatedNames[1])
                        .build());
            }

            films.add(new Film.FilmBuilder(content[0])
                    .director(new Director.DirectorBuilder(
                            content[1], content[2])
                            .build())
                    .types(typeList)
                    .productionYear(Integer.parseInt(content[4]))
                    .cast(cast)
                    .build()
            );
        }

        count = films.size();
    }

    public long getCount() {
        return count;
    }

    public Film getFilm(int index) {
        return films.get(index);
    }

    public Director getDirector(int index) {
        return getFilm(index).getDirector();
    }

    public String getDirectorFirstName(int index) {
        return getDirector(index).getFirstName();
    }

    public String getDirectorLastName(int index) {
        return getDirector(index).getLastName();
    }

    public List<Actor> getFilmCast(int index) {
        return getFilm(index).getCast();
    }

    public Actor getActorFromCast(int index, int actorIndex) {
        return getFilmCast(index).get(actorIndex);
    }
}
