package com.example.patrycja.filmbase.user_test;

import com.example.patrycja.filmbase.DTO.FavouritesDTO;
import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.example.patrycja.filmbase.request.AddFilmRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ReadAllFavouritesTest extends FillBaseTemplate {

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

    @WithUserDetails("admin")
    private void addToFavourites() throws Exception {
        for (int i = 1; i <= createdRequests.size(); i++) {
            this.mockMvc.perform(post("/films/{id}", i)
                    .param("action", "favourite"));
        }
    }

    @Test
    @WithUserDetails("admin")
    public void readAllFavouritesTest() throws Exception {
        addToFavourites();

        MvcResult mvcResult = this.mockMvc.perform(get("/users/{id}/favourites", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        FavouritesDTO favouritesDTO = gsonSerialize
                        .fromJson(mvcResult
                                .getResponse()
                                .getContentAsString(),
                                FavouritesDTO.class);
        List<FilmBriefDTO> responseFilms = favouritesDTO.getFilms();

        for (AddFilmRequest filmRequest : createdRequests) {
            assertTrue(responseFilms
                    .stream()
                    .anyMatch(film -> film.checkIfContentEquals(
                            new FilmBriefDTO(filmRequest.getDTO()))));
        }
    }
}
