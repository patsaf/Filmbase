package com.example.patrycja.filmbase.actor_test;

import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.example.patrycja.filmbase.request.AddActorRequest;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddNewActorTest extends FillBaseTemplate {

    private AddActorRequest actorRequest;

    @Before
    public void init() {
        initFilms();
        postFilms();
        List<FilmBriefDTO> filmBriefDTOList = new ArrayList<>();
        FilmBriefDTO film1 = new FilmBriefDTO();
        film1.setTitle("Lady Bird");
        film1.setProductionYear(2017);
        filmBriefDTOList.add(film1);
        actorRequest = new AddActorRequest("Laurie", "Metcalf",
                filmBriefDTOList, LocalDate.of(1955, Month.MAY, 26));
    }

    @Test
    public void addValidActor() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/actors", actorRequest, String.class);
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
    }
}
