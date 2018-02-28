package com.example.patrycja.filmbase.repository;

import com.example.patrycja.filmbase.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    Film findById(long id);
}
