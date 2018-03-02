package com.example.patrycja.filmbase.actor_test;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.DTO.DirectorDTO;
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
public class ReadOneActorTest extends FillBaseTemplate{

    @Before
    public void init() {
        initFilms();
        postFilms();
    }

    @Test
    public void checkIfResponseContainsActorWithGivenId() {
        for(int i=1; i<=requestGenerator.getCount(); i++) {
            ResponseEntity<ActorDTO> responseEntity = restTemplate.getForEntity("/actors/{id}", ActorDTO.class, i);
            assertThat(responseEntity
                    .getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            ActorDTO responseActor = responseEntity.getBody();
            assertTrue(responseActor
                    .getFirstName()
                    .equals(requestGenerator
                            .getActor(i-1)
                            .getFirstName()) &&
                    responseActor
                            .getLastName()
                            .equals(requestGenerator
                                    .getActor(i-1)
                                    .getLastName()
                            ));
        }
    }
}
