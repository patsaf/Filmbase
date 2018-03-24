package com.example.patrycja.filmbase.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
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
    private List<Film> favFilms;

    @OneToMany
    private List<Actor> favActors;

    @OneToMany
    private List<Director> favDirectors;

    @OneToMany
    private List<Film> filmWishlist;

    @ElementCollection
    private Map<Long, Double> ratedFilms;

    @ElementCollection
    private Map<Long, Double> ratedActors;

    @ElementCollection
    private Map<Long, Double> ratedDirectors;

    public User(UserBuilder builder) {
        this.username = builder.username;
        this.passoword = builder.passoword;
        this.email = builder.email;
        this.registerDate = builder.registerDate;
        isAdmin = false;
        initCollections();
    }

    private void initCollections() {
        favFilms = new ArrayList<>();
        favActors = new ArrayList<>();
        favDirectors = new ArrayList<>();
        filmWishlist = new ArrayList<>();
        ratedFilms = new HashMap<>();
        ratedActors = new HashMap<>();
        ratedDirectors = new HashMap<>();
    }

    public void makeAdmin() {
        isAdmin = true;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isAdmin) {
            return Collections.singletonList(new SimpleGrantedAuthority("ADMIN"));
        }
        return Collections.singletonList(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return passoword;
    }

    @Override
    public String getUsername() {
        return username;
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

    public static class UserBuilder {
        private String username;
        private String passoword;
        private String email;
        private LocalDate registerDate;

        public UserBuilder(String username) {
            this.username = username;
        }

        public UserBuilder password(String passoword) {
            this.passoword = passoword;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder registerDate(LocalDate registerDate) {
            this.registerDate = registerDate;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

}
