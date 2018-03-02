package com.example.patrycja.filmbase.actor_test;

import com.example.patrycja.filmbase.request.UpdateDateOfBirthRequest;
import com.example.patrycja.filmbase.template.FillBaseTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateActorDateOfBirthTest extends FillBaseTemplate {

    private UpdateDateOfBirthRequest updateRequest;

    @Before
    public void init() {
        initFilms();
        postFilms();
        updateRequest = new UpdateDateOfBirthRequest(
                LocalDate.of(1978, Month.JANUARY, 13));
    }

    @Test
    public void updateActorDateOfBirth() {
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(
                "/actors/{id}",
                updateRequest,
                Object.class,
                1);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }
}
