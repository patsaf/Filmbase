package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.example.patrycja.filmbase.model.Film;
import com.example.patrycja.filmbase.repository.FilmRepository;
import com.example.patrycja.filmbase.service.FilmGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class FilmController {

    @Autowired
    FilmRepository filmRepository;

    /*@PostConstruct
    public void initialize() {
        FilmGenerator filmGenerator = new FilmGenerator();
        for(int i=0; i<filmGenerator.getCount(); i++) {
            filmRepository.save(filmGenerator.getFilm(i));
        }
    }*/

    @GetMapping("/films")
    public List<Film> getAllFilms() { return filmRepository.findAll(); }

    @GetMapping("/films/{id}")
    public HttpEntity<Film> findFilm(@PathVariable("id") long id) {
        Film film = filmRepository.findById(id);
        if(film!=null) {
            return ResponseEntity.ok(film);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/films")
    public HttpEntity<List<Film>> addFilm(@Valid @RequestBody AddFilmRequest addFilmRequest) {
        Film newFilm = addFilmRequest.getFilm();
        for(Film film : filmRepository.findAll()) {
            if(newFilm.checkIfContentEquals(film)) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(filmRepository.findAll());
            }
        }
        filmRepository.save(newFilm);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(filmRepository.findAll());
    }
}
