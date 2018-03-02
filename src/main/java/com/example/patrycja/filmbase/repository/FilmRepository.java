package com.example.patrycja.filmbase.repository;

import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    Film findById(long id);
    Film findByTitleAndProductionYear(String title, int productionYear);

    @Transactional
    @Modifying
    @Query("update Film f set f.cast = ?1 where f.title = ?2 and f.productionYear = ?3")
    void setCastByTitleAndProductionYear(List<Actor> cast, String title, int productionYear);
}
