package com.example.patrycja.filmbase.repository;

import com.example.patrycja.filmbase.model.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    Director findByFirstNameAndLastName(String firstName, String lastName);

    Director findById(long id);

    @Transactional
    @Modifying
    @Query("update Director d set d.dateOfBirth = ?1 where d.id = ?2")
    void setDateOfBirthById(LocalDate dateOfBirth, long id);
}
