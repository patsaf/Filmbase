package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;

public class UserDTO {

    private long id;
    private String username;

    private LocalDate registerDate;

    public UserDTO() {}

    public UserDTO(long id, String username, LocalDate registerDate) {
        this.id = id;
        this.username = username;
        this.registerDate = registerDate;
    }

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        registerDate = user.getRegisterDate();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public boolean checkIfDataEquals(UserDTO userDTO) {
        return (username.equals(userDTO.getUsername()) &&
                registerDate.equals(userDTO.getRegisterDate()));
    }
}
