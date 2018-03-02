package com.example.patrycja.filmbase.actor_test;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.DTO.DirectorDTO;
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
public class ReadAllActorsTest extends FillBaseTemplate {

    @Before
    public void init() {
        initFilms();
        postFilms();
    }

    @Test
    public void checkIfReponseContainsAddedActors() {
        ResponseEntity<ActorDTO[]> responseEntity = restTemplate.getForEntity("/actors", ActorDTO[].class);
        assertThat(responseEntity
                .getStatusCode())
                .isEqualTo(HttpStatus.OK);

        List<ActorDTO> responseActors = Arrays.asList(responseEntity.getBody());
        for(AddFilmRequest filmRequest : createdRequests) {
            for(AddActorRequest actorRequest : filmRequest.getActorRequests()) {
                assertTrue(responseActors
                        .stream()
                        .anyMatch(actor -> actor.getFirstName().equals(actorRequest.getFirstName()) &&
                                actor.getLastName().equals(actorRequest.getLastName())));
            }
        }
    }
}
