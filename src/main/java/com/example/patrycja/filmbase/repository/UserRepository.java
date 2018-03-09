package com.example.patrycja.filmbase.repository;

import com.example.patrycja.filmbase.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findById(long id);
    User findByUsername(String username);
    User findByEmail(String email);
}
