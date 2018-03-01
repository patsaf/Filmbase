package com.example.patrycja.filmbase.repository;

import com.example.patrycja.filmbase.model.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    Director findByFirstNameAndLastName(String firstName, String lastName);
}
