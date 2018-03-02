package com.example.patrycja.filmbase;

import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
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

    private AddActorRequest actorRequest;
    private AddActorRequest invalidActorRequest;
    private AddActorRequest existingRequest;
    private AddActorRequest nonExistingFilmRequest;

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

        List<FilmBriefDTO> validList = new ArrayList<>();
        List<FilmBriefDTO> invalidList = new ArrayList<>();
        FilmBriefDTO film1 = new FilmBriefDTO();
        film1.setTitle("Leon");
        film1.setProductionYear(1994);
        FilmBriefDTO film2 = new FilmBriefDTO();
        film2.setTitle("Cinderella");
        film2.setProductionYear(2015);
        validList.add(film1);
        invalidList.add(film1);
        invalidList.add(film2);

        actorRequest = new AddActorRequest("Laurie", "Metcalf",
                validList, LocalDate.of(1955, Month.MAY, 26));
        invalidActorRequest = new AddActorRequest(null, "Bohnam-Carter");
        existingRequest = new AddActorRequest("Colin", "Firth");
        nonExistingFilmRequest = new AddActorRequest("Helena", "Bohnam-Carter",
                invalidList, LocalDate.of(1965, Month.MAY, 26));
    }

    @Test
    public void rejectRequestWithInvalidData() {
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity("/films", invalidRequest, Object.class);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        ResponseEntity<Object> responseEntityActor = restTemplate.postForEntity("/actors", invalidActorRequest,
                Object.class);
        assertThat(responseEntityActor.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void checkNonExistingId() {
        long id = 12345;
        ResponseEntity<Object> responseEntity = restTemplate.getForEntity("/films/{id}", Object.class, id);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        ResponseEntity<Object> directorResponseEntity = restTemplate.getForEntity("/directors/{id}",
                Object.class, id);
        assertThat(directorResponseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        ResponseEntity<Object> actorResponseEntity = restTemplate.getForEntity("/actors/{id}",
                Object.class, id);
        assertThat(actorResponseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void rejectDuplicateRequest() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/films",
                duplicateRequest, String.class);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> duplicateEntity = restTemplate.postForEntity("/films",
                duplicateRequest, String.class);
        assertThat(duplicateEntity.getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        ResponseEntity<String> responseEntityActor = restTemplate.postForEntity("/actors",
                actorRequest, String.class);
        assertThat(responseEntityActor.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> duplicateEntityActor = restTemplate.postForEntity("/actors",
                actorRequest, String.class);
        assertThat(duplicateEntityActor.getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        ResponseEntity<String> existingActorEntity = restTemplate.postForEntity("/films/{id}/cast",
                existingRequest, String.class, 1);
        assertThat(existingActorEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        ResponseEntity<String> existingActorEntityDuplicate = restTemplate.postForEntity("/films/{id}/cast",
                existingRequest, String.class, 1);
        assertThat(existingActorEntityDuplicate.getStatusCode())
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
        assertThat(responseEntityActor.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        ResponseEntity<String> duplicateResponseActor = restTemplate.postForEntity(
                "/actors/{id}",
                updateRequest,
                String.class,
                1);
        assertThat(duplicateResponseActor.getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void rejectActorWithNonExistingFilm() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/actors",
                nonExistingFilmRequest, String.class);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
