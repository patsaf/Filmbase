package com.example.patrycja.filmbase;

import com.example.patrycja.filmbase.request.AddActorRequest;
import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.example.patrycja.filmbase.request.UpdateDateOfBirthRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
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
    private UpdateDateOfBirthRequest updateRequest;

    @Before
    public void init() {
        List<AddActorRequest> actorRequests = new ArrayList<>();
        actorRequests.add(new AddActorRequest("Colin", "Firth"));
        invalidRequest = new AddFilmRequest(
                "Kingsman: The Golden Circle",
                null,
                17,
                "Matthew",
                "Vaughn",
                actorRequests
        );
        String[] types = {"Crime", "Drama", "Thriller"};
        duplicateRequest = new AddFilmRequest(
                "Leon",
                Arrays.asList(types),
                1994,
                "Luc",
                "Besson",
                actorRequests
        );

        updateRequest = new UpdateDateOfBirthRequest(
                LocalDate.of(1978, Month.JANUARY, 13));
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

        ResponseEntity<Object> directorResponseEntity = restTemplate.getForEntity("/directors/{id}", Object.class, id);
        assertThat(directorResponseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        ResponseEntity<Object> actorResponseEntity = restTemplate.getForEntity("/actors/{id}", Object.class, id);
        assertThat(actorResponseEntity.getStatusCode())
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

    @Test
    public void rejectUnnecessaryUpdate() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "/directors/{id}",
                updateRequest,
                String.class,
                1);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        ResponseEntity<String> duplicateResponse = restTemplate.postForEntity(
                "/directors/{id}",
                updateRequest,
                String.class,
                1);
        assertThat(duplicateResponse.getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        ResponseEntity<String> responseEntityActor = restTemplate.postForEntity(
                "/actors/{id}",
                updateRequest,
                String.class,
                1);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        ResponseEntity<String> duplicateResponseActor = restTemplate.postForEntity(
                "/actors/{id}",
                updateRequest,
                String.class,
                1);
        assertThat(duplicateResponse.getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }
}
