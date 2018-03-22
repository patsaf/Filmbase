package com.example.patrycja.filmbase.error_test;

import com.example.patrycja.filmbase.request.AddActorRequest;
import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.example.patrycja.filmbase.request.SignUpRequest;
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
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class InvalidDataTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;


    private AddFilmRequest invalidFilmRequest;
    private AddActorRequest invalidActorRequest;
    private SignUpRequest invalidUserRequest;

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
        invalidFilmRequest = new AddFilmRequest
                .AddFilmRequestBuilder("Kingsman: The Golden Circle")
                .types(null)
                .productionYear(17)
                .director("Matthew", "Vaughn")
                .actorRequests(actorRequests)
                .build();
        invalidActorRequest = new AddActorRequest
                .AddActorRequestBuilder(null, "Bohnam-Carter")
                .build();
        invalidUserRequest = new SignUpRequest("user", "user", "user");
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = {"USER"})
    public void rejectRequestWithInvalidData() throws Exception {
        Gson gson = new Gson();
        this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(invalidFilmRequest)))
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
}
