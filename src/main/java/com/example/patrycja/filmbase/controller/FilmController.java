package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.exception.DuplicateException;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.repository.DirectorRepository;
import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.example.patrycja.filmbase.model.Film;
import com.example.patrycja.filmbase.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FilmController {

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    DirectorRepository directorRepository;

    /*@PostConstruct
    public void initialize() {
        FilmGenerator filmGenerator = new FilmGenerator();
        for(int i=0; i<filmGenerator.getCount(); i++) {
            filmRepository.save(filmGenerator.getFilm(i));
        }
    }*/

    @GetMapping("/films")
    public List<FilmDTO> getAllFilms() {
        List<Film> allFilms = filmRepository.findAll();

        List<FilmDTO> allDTOs = new ArrayList<>();
        for(Film film : allFilms) {
            allDTOs.add(new FilmDTO(film));
        }

        return allDTOs;
    }

    @GetMapping("/films/{id}")
    public HttpEntity<FilmDTO> findFilm(@PathVariable("id") long id) {
        Film film = filmRepository.findById(id);
        if(film!=null) {
            return ResponseEntity.ok(new FilmDTO(film));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDTO addFilm(@Valid @RequestBody AddFilmRequest addFilmRequest) {

        Director director = directorRepository.findByFirstNameAndLastName(
                addFilmRequest.getDirectorFirstName(),
                addFilmRequest.getDirectorLastName());

        if(director==null) {
            director = new Director(
                    addFilmRequest.getDirectorFirstName(),
                    addFilmRequest.getDirectorLastName());
            directorRepository.save(director);
        }

        Film newFilm = new Film(
                addFilmRequest.getTitle(),
                director,
                addFilmRequest.getTypes(),
                addFilmRequest.getProductionYear());

        for(Film film : filmRepository.findAll()) {
            if(newFilm.checkIfContentEquals(film)) {
                throw new DuplicateException("Film already exists!");
            }
        }

        filmRepository.save(newFilm);
        return new FilmDTO(newFilm);
    }
}
