package com.example.patrycja.filmbase.film_test;

import com.example.patrycja.filmbase.DTO.FilmDTO;
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
public class ReadAllFilmsTest extends FillBaseTemplate {

    @Before
    public void init() {
        initFilms();
        postFilms();
    }

    @Test
    public void checkIfResponseContainsAddedFilms() {
        ResponseEntity<FilmDTO[]> responseEntity = restTemplate.getForEntity("/films", FilmDTO[].class);
        assertThat(responseEntity
                .getStatusCode())
                .isEqualTo(HttpStatus.OK);

        List<FilmDTO> responseFilms = Arrays.asList(responseEntity.getBody());
        for (AddFilmRequest filmRequest : createdRequests) {
            assertTrue(responseFilms
                    .stream()
                    .anyMatch(film -> film.checkIfContentEquals(filmRequest.getDTO())));
        }
    }

}
