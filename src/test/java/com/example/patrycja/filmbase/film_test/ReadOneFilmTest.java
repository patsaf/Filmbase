package com.example.patrycja.filmbase.film_test;

import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.template.FillBaseTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadOneFilmTest extends FillBaseTemplate {

    @Before
    public void init() {
        initFilms();
        postFilms();
    }

    @Test
    public void checkIfResponseContainsFilmWithGivenId() {
        for(int i=1; i<=requestGenerator.getCount(); i++) {
            ResponseEntity<FilmDTO> responseEntity = restTemplate.getForEntity("/films/{id}", FilmDTO.class, i);
            assertThat(responseEntity
                    .getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            FilmDTO responseFilm = responseEntity.getBody();
            assertTrue(responseFilm
                    .checkIfContentEquals(requestGenerator
                                    .getRequest(i-1)
                                    .getDTO()
                    ));
        }
    }
}
