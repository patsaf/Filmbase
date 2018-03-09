package com.example.patrycja.filmbase.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Size(min = 4, max = 25)
    private String username;

    @Size(min = 6)
    private String passoword;

    @Email
    private String email;
    private boolean isAdmin;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate registerDate;

    @OneToMany
    List<Film> favFilms;

    @OneToMany
    List<Actor> favActors;

    @OneToMany
    List<Director> favDirectors;

    public User() {
    }

    public User(String username, String passoword, String email, LocalDate registerDate) {
        this.username = username;
        this.passoword = passoword;
        this.email = email;
        this.registerDate = registerDate;
        isAdmin = false;
        favFilms = new ArrayList<>();
        favActors = new ArrayList<>();
        favDirectors = new ArrayList<>();
    }

    public void makeAdmin() {
        isAdmin = true;
    }

    public void addFilm(Film film) {
        favFilms.add(film);
    }

    public void addActor(Actor actor) {
        favActors.add(actor);
    }

    public void addDirector(Director director) {
        favDirectors.add(director);
    }

    public long getId() {
        return id;
    }

    public String getPassoword() {
        return passoword;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    @Override
    public String getPassword() {
        return passoword;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public List<Film> getFavFilms() {
        return favFilms;
    }

    public List<Actor> getFavActors() {
        return favActors;
    }

    public List<Director> getFavDirectors() {
        return favDirectors;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isAdmin) {
            return Collections.singletonList(new SimpleGrantedAuthority("ADMIN"));
        }
        return Collections.singletonList(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
