package com.example.patrycja.filmbase.film_test;

import com.example.patrycja.filmbase.request.AddActorRequest;
import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.google.gson.Gson;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AddNewFilmTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private AddFilmRequest filmRequest;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        List<AddActorRequest> actorRequests = new ArrayList<>();
        actorRequests.add(new AddActorRequest
                .AddActorRequestBuilder("Jean", "Reno")
                .build());
        String[] types = {"Crime", "Drama", "Thriller"};
        filmRequest = new AddFilmRequest
                .AddFilmRequestBuilder("Leon")
                .types(Arrays.asList(types))
                .productionYear(1994)
                .director("Luc", "Besson")
                .actorRequests(actorRequests)
                .build();
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = {"USER"})
    public void addValidFilm() throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(filmRequest);
        this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }
}
