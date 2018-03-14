package com.example.patrycja.filmbase.user_test;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.DTO.DirectorDTO;
import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.template.FillBaseTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UnrateTest extends FillBaseTemplate {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void init() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        setupParser();
        rate();
    }

    @WithUserDetails("admin")
    private void rate() throws Exception {
        this.mockMvc.perform(post("/films/{id}", 1)
                .param("action", "rate")
                .param("rating", "8.0"));

        this.mockMvc.perform(post("/actors/{id}", 1)
                .param("action", "rate")
                .param("rating", "8.0"));

        this.mockMvc.perform(post("/directors/{id}", 1)
                .param("action", "rate")
                .param("rating", "8.0"));
    }

    @Test
    @WithUserDetails("admin")
    public void unrateFilm() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/films/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        long countFilms = gsonDeserialize
                .fromJson(mvcResult
                                .getResponse()
                                .getContentAsString(),
                        FilmDTO.class)
                .getCount();

        MvcResult mvcResult1 = this.mockMvc.perform(delete("/users/{id}/rates", 1)
                .param("type", "film")
                .param("item", "1"))
                .andExpect(status().isOk())
                .andReturn();

        FilmDTO filmDTO = gsonDeserialize
                .fromJson(mvcResult1
                                .getResponse()
                                .getContentAsString(),
                        FilmDTO.class);

        assertTrue(filmDTO.getCount() == (countFilms - 1));
    }

    @Test
    @WithUserDetails("admin")
    public void unrateActor() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/actors/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        long countActors = gsonDeserialize
                .fromJson(mvcResult
                                .getResponse()
                                .getContentAsString(),
                        ActorDTO.class)
                .getCount();

        MvcResult mvcResult1 = this.mockMvc.perform(delete("/users/{id}/rates", 1)
                .param("type", "actor")
                .param("item", "1"))
                .andExpect(status().isOk())
                .andReturn();

        ActorDTO actorDTO = gsonDeserialize
                .fromJson(mvcResult1
                                .getResponse()
                                .getContentAsString(),
                        ActorDTO.class);

        assertTrue(actorDTO.getCount() == (countActors - 1));
    }

    @Test
    @WithUserDetails("admin")
    public void unrateDirector() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/directors/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        long countDirectors = gsonDeserialize
                .fromJson(mvcResult
                                .getResponse()
                                .getContentAsString(),
                        DirectorDTO.class)
                .getCount();

        MvcResult mvcResult1 = this.mockMvc.perform(delete("/users/{id}/rates", 1)
                .param("type", "director")
                .param("item", "1"))
                .andExpect(status().isOk())
                .andReturn();

        DirectorDTO directorDTO = gsonDeserialize
                .fromJson(mvcResult1
                                .getResponse()
                                .getContentAsString(),
                        DirectorDTO.class);

        assertTrue(directorDTO.getCount() == (countDirectors - 1));
    }
}
