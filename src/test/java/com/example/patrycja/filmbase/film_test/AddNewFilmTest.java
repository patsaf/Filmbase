package com.example.patrycja.filmbase.film_test;

import com.example.patrycja.filmbase.request.AddFilmRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddNewFilmTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private AddFilmRequest filmRequest;

    @Before
    public void init() {
        String[] types = {"Crime", "Drama", "Thriller"};
        filmRequest = new AddFilmRequest("Leon", Arrays.asList(types), 1994,
                "Luc", "Besson");
    }

    @Test
    public void addValidFilm() {
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity("/films", filmRequest, Object.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
