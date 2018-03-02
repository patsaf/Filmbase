package com.example.patrycja.filmbase.controller;

import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.exception.DuplicateException;
import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.repository.ActorRepository;
import com.example.patrycja.filmbase.repository.DirectorRepository;
import com.example.patrycja.filmbase.request.AddActorRequest;
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
import java.util.ArrayList;
import java.util.List;

@RestController
public class FilmController {

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    DirectorRepository directorRepository;

    @Autowired
    ActorRepository actorRepository;

    @PostConstruct
    public void initialize() {
        FilmGenerator filmGenerator = new FilmGenerator();
        for(int i=0; i<filmGenerator.getCount(); i++) {
            Director director = directorRepository.findByFirstNameAndLastName(
                    filmGenerator.getFilm(i).getDirector().getFirstName(),
                    filmGenerator.getFilm(i).getDirector().getLastName());

            if(director==null) {
                directorRepository.save(filmGenerator.getFilm(i).getDirector());
            }

            List<Actor> cast = filmGenerator.getFilm(i).getCast();
            for(int j=0; j<cast.size(); j++) {
                Actor actor = actorRepository.findByFirstNameAndLastName(
                        cast.get(j).getFirstName(),
                        cast.get(j).getLastName());

                if(actor==null) {
                    actorRepository.save(cast.get(j));
                }
            }

            filmRepository.save(filmGenerator.getFilm(i));
        }
    }

    @GetMapping("/films")
    public List<FilmDTO> getAllFilms() {
        List<FilmDTO> allDTOs = new ArrayList<>();
        for(Film film : filmRepository.findAll()) {
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

        List<Actor> cast = new ArrayList<>();
        for(AddActorRequest actorRequest : addFilmRequest.getActorRequests()) {
            Actor actor = actorRepository.findByFirstNameAndLastName(
                    actorRequest.getFirstName(),
                    actorRequest.getLastName());

            if(actor==null) {
                actor = new Actor(
                        actorRequest.getFirstName(),
                        actorRequest.getLastName());
                actorRepository.save(actor);
            }
            cast.add(actor);
        }

        Film newFilm = new Film(
                addFilmRequest.getTitle(),
                director,
                addFilmRequest.getTypes(),
                addFilmRequest.getProductionYear(),
                cast);

        for(Film film : filmRepository.findAll()) {
            if(newFilm.checkIfContentEquals(film)) {
                throw new DuplicateException("Film already exists!");
            }
        }

        filmRepository.save(newFilm);
        return new FilmDTO(newFilm);
    }
}
