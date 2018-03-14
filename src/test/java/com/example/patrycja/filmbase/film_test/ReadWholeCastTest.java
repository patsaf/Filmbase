package com.example.patrycja.filmbase.film_test;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.request.AddActorRequest;
import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.example.patrycja.filmbase.template.FillBaseTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ReadWholeCastTest extends FillBaseTemplate {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        setupParser();
        initFilms();
    }

    @Test
    public void checkIfResponseContainsAddedActors() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/films/{id}/cast", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<ActorDTO> actorDTOList = Arrays.asList(gsonSerialize
                .fromJson(mvcResult
                        .getResponse()
                        .getContentAsString(),
                        ActorDTO[].class));
        AddFilmRequest request = requestGenerator.getRequest(0);
        for (AddActorRequest actorRequest : request.getActorRequests()) {
            assertTrue(actorDTOList
                    .stream()
                    .anyMatch(actor -> actor.getFirstName().equals(actorRequest.getFirstName()) &&
                            actor.getLastName().equals(actorRequest.getLastName())));
        }
    }
}
