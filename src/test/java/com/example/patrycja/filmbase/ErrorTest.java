package com.example.patrycja.filmbase;

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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ErrorTest extends FillBaseTemplate{

    private AddFilmRequest invalidRequest;
    private AddFilmRequest duplicateRequest;

    @Before
    public void init() {
        initFilms();
        postFilms();
        invalidRequest = new AddFilmRequest(
                "Kingsman: The Golden Circle",
                "Matthew Vaughn",
                null,
                17
        );
        String[] types = {"Crime", "Drama", "Thriller"};
        duplicateRequest = new AddFilmRequest(
                "Leon",
                "Luc Besson",
                Arrays.asList(types),
                1994
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
        long id = requestGenerator.getCount()+34;
        ResponseEntity<Object> responseEntity = restTemplate.getForEntity("/films/{id}", Object.class, id);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void rejectDuplicateRequest() {
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity("/films", duplicateRequest, Object.class);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Object> duplicateEntity = restTemplate.postForEntity("/films", duplicateRequest, Object.class);
        assertThat(duplicateEntity.getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

}
