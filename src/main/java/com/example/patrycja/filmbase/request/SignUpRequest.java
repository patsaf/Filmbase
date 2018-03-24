package com.example.patrycja.filmbase.request;

import com.example.patrycja.filmbase.DTO.UserDTO;
import com.example.patrycja.filmbase.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotNull
    @Size(min = 4, max = 25)
    private String username;

    @NotNull
    @Size(min = 6)
    private String password;

    @NotNull
    @Email
    private String email;

    public User getUser() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.now();
        String date = localDate.format(formatter);
        return new User.UserBuilder(username)
                .password(password)
                .email(email)
                .registerDate(LocalDate.parse(date, formatter))
                .build();
    }

    public UserDTO getDTO() {
        return new UserDTO(getUser());
    }
}
