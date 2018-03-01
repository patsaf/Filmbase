package com.example.patrycja.filmbase;

import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.model.Film;
import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.example.patrycja.filmbase.template.FillBaseTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ErrorTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private AddFilmRequest invalidRequest;
    private AddFilmRequest duplicateRequest;

    @Before
    public void init() {
        invalidRequest = new AddFilmRequest(
                "Kingsman: The Golden Circle",
                null,
                17,
                "Matthew",
                "Vaughn"
        );
        String[] types = {"Crime", "Drama", "Thriller"};
        duplicateRequest = new AddFilmRequest(
                "Leon",
                Arrays.asList(types),
                1994,
                "Luc",
                "Besson"
        );
    }

    @Test
    public void rejectRequestWithInvalidData() {
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity("/films", invalidRequest, Object.class);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void checkNonExistingId() {
        long id = 12345;
        ResponseEntity<Object> responseEntity = restTemplate.getForEntity("/films/{id}", Object.class, id);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void rejectDuplicateRequest() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/films", duplicateRequest, String.class);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> duplicateEntity = restTemplate.postForEntity("/films", duplicateRequest, String.class);
        assertThat(duplicateEntity.getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }
}
