package com.example.patrycja.filmbase.repository;

import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    Film findById(long id);

    Film findByTitleAndProductionYear(String title, int productionYear);

    @Transactional
    @Query("select f.cast from Film f where f.id = ?1")
    List<Actor> getCastById(long id);
}
