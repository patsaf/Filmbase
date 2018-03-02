package com.example.patrycja.filmbase.director_test;

import com.example.patrycja.filmbase.DTO.DirectorDTO;
import com.example.patrycja.filmbase.DTO.FilmDTO;
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
public class ReadOneDirectorTest extends FillBaseTemplate {

    @Before
    public void init() {
        initFilms();
        postFilms();
    }

    @Test
    public void checkIfResponseContainsDirectorWithGivenId() {
        for(int i=1; i<=requestGenerator.getCount(); i++) {
            ResponseEntity<DirectorDTO> responseEntity = restTemplate.getForEntity("/directors/{id}", DirectorDTO.class, i);
            assertThat(responseEntity
                    .getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            DirectorDTO responseDirector = responseEntity.getBody();
            assertTrue(responseDirector
                    .getFirstName()
                    .equals(requestGenerator
                            .getRequest(i-1)
                            .getDirectorFirstName()) &&
                    responseDirector
                            .getLastName()
                            .equals(requestGenerator
                                    .getRequest(i-1)
                                    .getDirectorLastName()
            ));
        }
    }
}
