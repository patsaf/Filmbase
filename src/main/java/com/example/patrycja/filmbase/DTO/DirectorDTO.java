package com.example.patrycja.filmbase.DTO;

import com.example.patrycja.filmbase.model.Director;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DirectorDTO {

    private long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private List<FilmBriefDTO> films;
    private double rate;
    private long count;

    public DirectorDTO(Director director) {
        this.id = director.getId();
        this.firstName = director.getFirstName();
        this.lastName = director.getLastName();
        this.dateOfBirth = director.getDateOfBirth();
        films = new ArrayList<>();
        director.getFilms()
                .forEach(film -> films.add(new FilmBriefDTO(film)));
        this.rate = director.getRate();
        this.count = director.getCount();
    }

    public Boolean checkIfDataEquals(DirectorDTO directorDTO) {
        return firstName.equals(directorDTO.getFirstName()) &&
                lastName.equals(directorDTO.getLastName()) &&
                dateOfBirth == directorDTO.getDateOfBirth() &&
                rate == directorDTO.getRate() &&
                count == directorDTO.getCount();
    }
}
