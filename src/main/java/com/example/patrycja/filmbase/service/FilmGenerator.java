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
    private List<Film> filmList;
    private long count;

    public FilmGenerator() {
        filmList = new ArrayList<>();
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
                cast.add(new Actor(separatedNames[0], separatedNames[1]));
            }

            filmList.add(new Film(
                    content[0],
                    new Director(content[1], content[2]),
                    typeList,
                    Integer.parseInt(content[4]),
                    cast
            ));
        }

        count = filmList.size();
    }

    public long getCount() {
        return count;
    }

    public Film getFilm(int index) {
        return filmList.get(index);
    }
}
