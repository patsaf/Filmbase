package com.example.patrycja.filmbase.user_test;

import com.example.patrycja.filmbase.DTO.*;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserFeaturesTest extends FillBaseTemplate {

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
        postFilms();
    }

    @WithMockUser(username = "test", password = "test", roles = {"USER"})
    protected void postFilms() {
        List<String> jsons = new ArrayList<>();
        createdRequests.forEach(filmRequest -> jsons.add(gsonSerialize.toJson(filmRequest)));
        jsons.forEach(json -> {
            try {
                this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    @WithUserDetails("admin")
    public void addToFavouritesTest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/films/{id}", 3)
                .param("action", "favourite"))
                .andExpect(status().isOk())
                .andReturn();

        FilmDTO filmDTO = gsonDeserialize
                .fromJson(mvcResult
                                .getResponse()
                                .getContentAsString(),
                        FilmDTO.class);

        MvcResult mvcResult1 = this.mockMvc.perform(post("/actors/{id}", 3)
                .param("action", "favourite"))
                .andExpect(status().isOk())
                .andReturn();

        ActorDTO actorDTO = gsonDeserialize
                .fromJson(mvcResult1
                                .getResponse()
                                .getContentAsString(),
                        ActorDTO.class);

        MvcResult mvcResult2 = this.mockMvc.perform(post("/directors/{id}", 3)
                .param("action", "favourite"))
                .andExpect(status().isOk())
                .andReturn();

        DirectorDTO directorDTO = gsonDeserialize
                .fromJson(mvcResult2
                                .getResponse()
                                .getContentAsString(),
                        DirectorDTO.class);

        MvcResult mvcResult3 = this.mockMvc.perform(get("/users/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDTO userDTO = gsonDeserialize
                .fromJson(mvcResult3
                                .getResponse()
                                .getContentAsString(),
                        UserDTO.class);

        assertTrue(userDTO
                .getFavFilms()
                .stream()
                .anyMatch(film -> film.checkIfContentEquals(new FilmBriefDTO(filmDTO))));

        assertTrue(userDTO
                .getFavActors()
                .stream()
                .anyMatch(actor -> actor.checkIfDataEquals(actorDTO)));

        assertTrue(userDTO
                .getFavDirectors()
                .stream()
                .anyMatch(director -> director.checkIfDataEquals(directorDTO)));
    }

    @Test
    @WithUserDetails("admin")
    public void addToWishlistTest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/films/{id}", 3)
                .param("action", "wishlist"))
                .andExpect(status().isOk())
                .andReturn();

        FilmDTO filmDTO = gsonDeserialize
                .fromJson(mvcResult
                                .getResponse()
                                .getContentAsString(),
                        FilmDTO.class);

        MvcResult mvcResult1 = this.mockMvc.perform(get("/users/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDTO userDTO = gsonDeserialize
                .fromJson(mvcResult1
                                .getResponse()
                                .getContentAsString(),
                        UserDTO.class);

        assertTrue(userDTO
                .getFilmWishlist()
                .stream()
                .anyMatch(film -> film.checkIfContentEquals(new FilmBriefDTO(filmDTO))));
    }

    @Test
    @WithUserDetails("admin")
    public void rateTest() throws Exception {
        MvcResult mvcResult3 = this.mockMvc.perform(get("/films/{id}", 3)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        long countFilms = gsonDeserialize
                .fromJson(mvcResult3
                                .getResponse()
                                .getContentAsString(),
                        FilmDTO.class)
                .getCount();

        MvcResult mvcResult4 = this.mockMvc.perform(get("/actors/{id}", 3)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        long countActors = gsonDeserialize
                .fromJson(mvcResult4
                                .getResponse()
                                .getContentAsString(),
                        FilmDTO.class)
                .getCount();

        MvcResult mvcResult5 = this.mockMvc.perform(get("/directors/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        long countDirectors = gsonDeserialize
                .fromJson(mvcResult5
                                .getResponse()
                                .getContentAsString(),
                        FilmDTO.class)
                .getCount();

        MvcResult mvcResult = this.mockMvc.perform(post("/films/{id}", 3)
                .param("action", "rate")
                .param("rating", "8.0"))
                .andExpect(status().isOk())
                .andReturn();

        FilmDTO filmDTO = gsonDeserialize
                .fromJson(mvcResult
                                .getResponse()
                                .getContentAsString(),
                        FilmDTO.class);

        assertTrue(filmDTO.getCount() == (countFilms + 1));

        MvcResult mvcResult1 = this.mockMvc.perform(post("/actors/{id}", 3)
                .param("action", "rate")
                .param("rating", "8.0"))
                .andExpect(status().isOk())
                .andReturn();

        ActorDTO actorDTO = gsonDeserialize
                .fromJson(mvcResult1
                                .getResponse()
                                .getContentAsString(),
                        ActorDTO.class);

        assertTrue(actorDTO.getCount() == (countActors + 1));

        MvcResult mvcResult2 = this.mockMvc.perform(post("/directors/{id}", 1)
                .param("action", "rate")
                .param("rating", "8.0"))
                .andExpect(status().isOk())
                .andReturn();

        DirectorDTO directorDTO = gsonDeserialize
                .fromJson(mvcResult2
                                .getResponse()
                                .getContentAsString(),
                        DirectorDTO.class);

        assertTrue(directorDTO.getCount() == (countDirectors + 1));
    }
}
