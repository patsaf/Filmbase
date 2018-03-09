package com.example.patrycja.filmbase.request;

import com.example.patrycja.filmbase.DTO.UserDTO;
import com.example.patrycja.filmbase.model.User;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SignUpRequest {

    @NotNull
    @Size(min=4, max=25)
    private String username;

    @NotNull
    @Size(min=6)
    private String password;

    @NotNull
    @Email
    private String email;

    public SignUpRequest() {}

    public SignUpRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User getUser() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.now();
        String date = localDate.format(formatter);
        return new User(username, password, email, LocalDate.parse(date, formatter));
    }

    public UserDTO getDTO() { return new UserDTO(getUser()); }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
