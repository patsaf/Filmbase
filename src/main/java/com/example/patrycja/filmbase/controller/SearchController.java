package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.model.Film;
import com.example.patrycja.filmbase.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SearchController {

    @Autowired
    FilmRepository filmRepository;

    @PostMapping("/films/search")
    public List<FilmDTO> filterResults(@RequestParam(value = "rateFrom", required = false) Double rateFrom,
                                       @RequestParam(value = "rateTo", required = false) Double rateTo,
                                       @RequestParam(value = "yearFrom", required = false) Integer yearFrom,
                                       @RequestParam(value = "yearTo", required = false) Integer yearTo,
                                       @RequestParam(value = "votesFrom", required = false) Long votesFrom,
                                       @RequestParam(value = "votesTo", required = false) Long votesTo,
                                       @RequestParam(value = "type", required = false) String[] types) {

        List<Film> filteredList = filmRepository.findAll();

        if (rateFrom != null) {
            filteredList = filteredList
                    .stream()
                    .filter(film -> film.getRate() >= rateFrom)
                    .collect(Collectors.toList());
        }

        if (rateTo != null) {
            filteredList = filteredList
                    .stream()
                    .filter(film -> film.getRate() <= rateTo)
                    .collect(Collectors.toList());
        }

        if (yearFrom != null) {
            filteredList = filteredList
                    .stream()
                    .filter(film -> film.getProductionYear() >= yearFrom)
                    .collect(Collectors.toList());
        }

        if (yearTo != null) {
            filteredList = filteredList
                    .stream()
                    .filter(film -> film.getProductionYear() >= yearTo)
                    .collect(Collectors.toList());
        }

        if (votesFrom != null) {
            filteredList = filteredList
                    .stream()
                    .filter(film -> film.getCount() >= votesFrom)
                    .collect(Collectors.toList());
        }

        if (votesTo != null) {
            filteredList = filteredList
                    .stream()
                    .filter(film -> film.getCount() <= votesTo)
                    .collect(Collectors.toList());
        }

        if (types != null) {
            for (String type : types) {
                filteredList = filteredList
                        .stream()
                        .filter(film -> film.getTypes().contains(type))
                        .collect(Collectors.toList());
            }
        }

        List<FilmDTO> filteredListDTO = new ArrayList<>();
        filteredList
                .forEach(film -> filteredListDTO.add(new FilmDTO(film)));
        return filteredListDTO;
    }
}
