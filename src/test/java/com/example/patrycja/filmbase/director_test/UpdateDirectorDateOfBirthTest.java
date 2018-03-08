package com.example.patrycja.filmbase.director_test;

import com.example.patrycja.filmbase.request.UpdateDateOfBirthRequest;
import com.example.patrycja.filmbase.template.FillBaseTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateDirectorDateOfBirthTest extends FillBaseTemplate {

    private UpdateDateOfBirthRequest updateRequest;

    @Before
    public void init() {
        initFilms();
        postFilms();
        updateRequest = new UpdateDateOfBirthRequest(
                LocalDate.of(1978, Month.JANUARY, 13));
    }

    @Test
    public void updateDirectorDateOfBirth() {
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(
                "/directors/{id}",
                updateRequest,
                Object.class,
                1);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }
}
