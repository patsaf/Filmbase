package com.example.patrycja.filmbase.film_test;

import com.example.patrycja.filmbase.request.AddActorRequest;
import com.example.patrycja.filmbase.template.FillBaseTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddActorToCastTest extends FillBaseTemplate {

    private AddActorRequest actorRequest;

    @Before
    public void init() {
        initFilms();
        postFilms();
        actorRequest = new AddActorRequest("Laurie", "Metcalf");
    }

    @Test
    public void addNewActorToCastTest() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/films/{id}/cast", actorRequest,
                String.class, 5);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }
}
