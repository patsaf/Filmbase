package com.example.patrycja.filmbase;

import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.example.patrycja.filmbase.request.AddActorRequest;
import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.example.patrycja.filmbase.request.SignUpRequest;
import com.example.patrycja.filmbase.request.UpdateDateOfBirthRequest;
import com.example.patrycja.filmbase.template.LocalDateSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ErrorTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private AddFilmRequest invalidRequest;
    private AddFilmRequest duplicateRequest;

    private UpdateDateOfBirthRequest updateRequest;

    private AddActorRequest actorRequest;
    private AddActorRequest invalidActorRequest;
    private AddActorRequest existingRequest;
    private AddActorRequest nonExistingFilmRequest;

    private SignUpRequest userRequest;
    private SignUpRequest invalidUserRequest;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        List<AddActorRequest> actorRequests = new ArrayList<>();
        actorRequests.add(new AddActorRequest("Colin", "Firth"));
        invalidRequest = new AddFilmRequest(
                "Kingsman: The Golden Circle",
                null,
                17,
                "Matthew",
                "Vaughn",
                actorRequests
        );
        String[] types = {"Crime", "Drama", "Thriller"};
        duplicateRequest = new AddFilmRequest(
                "Leon",
                Arrays.asList(types),
                1994,
                "Luc",
                "Besson",
                actorRequests
        );

        updateRequest = new UpdateDateOfBirthRequest(
                LocalDate.of(1978, Month.JANUARY, 13));

        List<FilmBriefDTO> validList = new ArrayList<>();
        List<FilmBriefDTO> invalidList = new ArrayList<>();
        FilmBriefDTO film1 = new FilmBriefDTO();
        film1.setTitle("Leon");
        film1.setProductionYear(1994);
        FilmBriefDTO film2 = new FilmBriefDTO();
        film2.setTitle("Cinderella");
        film2.setProductionYear(2015);
        validList.add(film1);
        invalidList.add(film1);
        invalidList.add(film2);

        actorRequest = new AddActorRequest("Laurie", "Metcalf",
                validList, LocalDate.of(1955, Month.MAY, 26));
        invalidActorRequest = new AddActorRequest(null, "Bohnam-Carter");
        existingRequest = new AddActorRequest("Colin", "Firth");
        nonExistingFilmRequest = new AddActorRequest("Helena", "Bohnam-Carter",
                invalidList, LocalDate.of(1965, Month.MAY, 26));
        userRequest = new SignUpRequest("user", "password", "user@test.mail");
        invalidUserRequest = new SignUpRequest("user", "user", "user");
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = {"USER"})
    public void rejectRequestWithInvalidData() throws Exception {
        Gson gson = new Gson();
        this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(invalidRequest)))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/actors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(invalidActorRequest)))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(invalidUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = {"USER"})
    public void checkNonExistingId() throws Exception {
        long id = 12345;
        this.mockMvc.perform(get("/films/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(get("/directors/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(get("/actors/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(get("/users/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
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

    @Test
    @WithMockUser(username = "test", password = "test", roles = {"USER"})
    public void rejectUnnecessaryUpdate() throws Exception {

        String action = "update";
        String birthday = "21-01-1996";

        this.mockMvc.perform(post("/directors/{id}", 1)
                .param("action", action)
                .param("birthday", birthday))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/directors/{id}", 1)
                .param("action", action)
                .param("birthday", birthday))
                .andExpect(status().isConflict());

        this.mockMvc.perform(post("/actors/{id}", 1)
                .param("action", action)
                .param("birthday", birthday))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/actors/{id}", 1)
                .param("action", action)
                .param("birthday", birthday))
                .andExpect(status().isConflict());
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

    @Test
    @WithMockUser(username = "test", password = "test", roles = {"USER"})
    public void rejectInvalidParam() throws Exception {
        this.mockMvc.perform(post("/films/{id}", 1)
                .param("action", "random"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/actors/{id}", 1)
                .param("action", "random"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/directors/{id}", 1)
                .param("action", "random"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/films/{id}", 1)
                .param("action", "rate")
                .param("rating", "12.0"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/actors/{id}", 1)
                .param("action", "rate")
                .param("rating", "12.0"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/directors/{id}", 1)
                .param("action", "rate")
                .param("rating", "12.0"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/films/{id}", 1)
                .param("action", "rate"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/actors/{id}", 1)
                .param("action", "rate"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/directors/{id}", 1)
                .param("action", "rate"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/actors/{id}", 2)
                .param("action", "update"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/directors/{id}", 2)
                .param("action", "update"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/actors/{id}", 2)
                .param("action", "update")
                .param("birthday", "21/01/1996"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/directors/{id}", 2)
                .param("action", "update")
                .param("birthday", "1996-01-21"))
                .andExpect(status().isBadRequest());
    }
}
