package com.example.patrycja.filmbase.director_test;

import com.example.patrycja.filmbase.DTO.DirectorDTO;
import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.example.patrycja.filmbase.template.FillBaseTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadAllDirectorsTest extends FillBaseTemplate {

    @Before
    public void init() {
        initFilms();
        postFilms();
    }

    @Test
    public void checkIfReponseContainsAddedDirectorss() {
        ResponseEntity<DirectorDTO[]> responseEntity = restTemplate.getForEntity("/directors", DirectorDTO[].class);
        assertThat(responseEntity
                .getStatusCode())
                .isEqualTo(HttpStatus.OK);

        List<DirectorDTO> responseFilms = Arrays.asList(responseEntity.getBody());
        for(AddFilmRequest filmRequest : createdRequests) {
            assertTrue(responseFilms
                    .stream()
                    .anyMatch(film -> film.getFirstName().equals(filmRequest.getDirectorFirstName()) &&
                    film.getLastName().equals(filmRequest.getDirectorLastName())));
        }
    }

}
