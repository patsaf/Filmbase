package com.example.patrycja.filmbase.film_test;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.request.AddActorRequest;
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
public class ReadWholeCastTest extends FillBaseTemplate {

    @Before
    public void init() {
        initFilms();
        postFilms();
    }

    @Test
    public void checkIfResponseContainsAddedActors() {
        ResponseEntity<ActorDTO[]> responseEntity = restTemplate.getForEntity("/films/{id}/cast", ActorDTO[].class, 1);
        assertThat(responseEntity
                .getStatusCode())
                .isEqualTo(HttpStatus.OK);

        List<ActorDTO> actorDTOList = Arrays.asList(responseEntity.getBody());
        AddFilmRequest request = requestGenerator.getRequest(0);
        for (AddActorRequest actorRequest : request.getActorRequests()) {
            assertTrue(actorDTOList
                    .stream()
                    .anyMatch(actor -> actor.getFirstName().equals(actorRequest.getFirstName()) &&
                            actor.getLastName().equals(actorRequest.getLastName())));
        }
    }

}
