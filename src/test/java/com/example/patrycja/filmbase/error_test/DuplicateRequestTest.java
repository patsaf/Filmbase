package com.example.patrycja.filmbase.error_test;

import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.example.patrycja.filmbase.request.AddActorRequest;
import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.example.patrycja.filmbase.request.SignUpRequest;
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
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DuplicateRequestTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private AddFilmRequest duplicateRequest;
    private AddActorRequest actorRequest;
    private AddActorRequest existingRequest;
    private SignUpRequest userRequest;


    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        List<AddActorRequest> actorRequests = new ArrayList<>();
        actorRequests.add(new AddActorRequest
                .AddActorRequestBuilder("Colin", "Firth")
                .build());

        String[] types = {"Crime", "Drama", "Thriller"};
        duplicateRequest = new AddFilmRequest
                .AddFilmRequestBuilder("Leon")
                .types(Arrays.asList(types))
                .productionYear(1994)
                .director("Luc", "Besson")
                .actorRequests(actorRequests)
                .build();

        List<FilmBriefDTO> validList = new ArrayList<>();
        FilmBriefDTO film = new FilmBriefDTO();
        film.setTitle("Leon");
        film.setProductionYear(1994);
        validList.add(film);

        actorRequest = new AddActorRequest
                .AddActorRequestBuilder("Laurie", "Metcalf")
                .films(validList)
                .dateOfBirth(LocalDate.of(1955, Month.MAY, 26))
                .build();
        existingRequest = new AddActorRequest
                .AddActorRequestBuilder("Colin", "Firth")
                .build();
        userRequest = new SignUpRequest("user", "password", "user@test.mail");
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = {"USER"})
    public void rejectDuplicateRequest() throws Exception {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .create();
        this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(duplicateRequest)))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(duplicateRequest)))
                .andExpect(status().isConflict());

        this.mockMvc.perform(post("/actors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(actorRequest)))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/actors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(actorRequest)))
                .andExpect(status().isConflict());

        this.mockMvc.perform(post("/films/{id}/cast", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(existingRequest)))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/films/{id}/cast", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(existingRequest)))
                .andExpect(status().isConflict());

        this.mockMvc.perform(post("/register", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(userRequest)))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/register", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(userRequest)))
                .andExpect(status().isConflict());
    }
}
