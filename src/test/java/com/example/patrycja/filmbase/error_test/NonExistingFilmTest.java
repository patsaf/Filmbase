package com.example.patrycja.filmbase.error_test;

import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.example.patrycja.filmbase.request.AddActorRequest;
import com.example.patrycja.filmbase.template.LocalDateSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class NonExistingFilmTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private AddActorRequest nonExistingFilmRequest;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        List<FilmBriefDTO> invalidList = new ArrayList<>();
        FilmBriefDTO film1 = new FilmBriefDTO();
        film1.setTitle("Leon");
        film1.setProductionYear(1994);
        FilmBriefDTO film2 = new FilmBriefDTO();
        film2.setTitle("Cinderella");
        film2.setProductionYear(2015);
        invalidList.add(film1);
        invalidList.add(film2);

        nonExistingFilmRequest = new AddActorRequest("Helena", "Bohnam-Carter",
                invalidList, LocalDate.of(1965, Month.MAY, 26));
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = {"USER"})
    public void rejectActorWithNonExistingFilm() throws Exception {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .create();
        this.mockMvc.perform(post("/actors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(nonExistingFilmRequest)))
                .andExpect(status().isNotFound());
    }
}
