package com.example.patrycja.filmbase.request;

import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class AddActorRequest {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private List<FilmBriefDTO> films;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dateOfBirth;

    public AddActorRequest(AddActorRequestBuilder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.films = builder.films;
        this.dateOfBirth = builder.dateOfBirth;
    }

    public static class AddActorRequestBuilder {
        private String firstName;
        private String lastName;
        private List<FilmBriefDTO> films;
        private LocalDate dateOfBirth;

        public AddActorRequestBuilder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public AddActorRequestBuilder films(List<FilmBriefDTO> films) {
            this.films = films;
            return this;
        }

        public AddActorRequestBuilder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public AddActorRequest build() {
            return new AddActorRequest(this);
        }
    }
}
