package com.example.patrycja.filmbase.user_test;

import com.example.patrycja.filmbase.DTO.FavouritesDTO;
import com.example.patrycja.filmbase.DTO.FilmBriefDTO;
import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.DTO.WishlistDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ManagePreferencesTest extends FillBaseTemplate {

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
    public void wishlistFavouritesMoveTest() throws Exception {
        this.mockMvc.perform(post("/films/{id}", 4)
                .param("action", "wishlist"))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/films/{id}", 4)
                .param("action", "favourite"))
                .andExpect(status().isOk());

        MvcResult mvcResult1 = this.mockMvc.perform(get("/films/{id}", 4)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        FilmDTO filmDTO = gsonDeserialize.fromJson(mvcResult1
                        .getResponse()
                        .getContentAsString(),
                FilmDTO.class);

        FilmBriefDTO filmBriefDTO = new FilmBriefDTO(filmDTO);

        MvcResult mvcResult = this.mockMvc.perform(get("/users/{id}/wishlist", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        WishlistDTO wishlistDTO = gsonDeserialize.fromJson(mvcResult
                        .getResponse()
                        .getContentAsString(),
                WishlistDTO.class);

        assertFalse(wishlistDTO.getFilms().contains(filmBriefDTO));

        this.mockMvc.perform(post("/films/{id}", 3)
                .param("action", "wishlist"))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/users/{id}/wishlist", 1)
                .param("item", "3"))
                .andExpect(status().isOk());

        MvcResult mvcResult2 = this.mockMvc.perform(get("/users/{id}/wishlist", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        WishlistDTO wishlistDTO1 = gsonDeserialize
                .fromJson(mvcResult2
                                .getResponse()
                                .getContentAsString(),
                        WishlistDTO.class);

        MvcResult mvcResult3 = this.mockMvc.perform(get("/films/{id}", 3)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        FilmDTO filmDTO1 = gsonDeserialize
                .fromJson(mvcResult3
                                .getResponse()
                                .getContentAsString(),
                        FilmDTO.class);

        FilmBriefDTO filmBriefDTO1 = new FilmBriefDTO(filmDTO1);

        assertFalse(wishlistDTO1.getFilms().contains(filmBriefDTO1));
    }

    @Test
    @WithUserDetails("admin")
    public void deleteFromFavouritesTest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/films/{id}", 3)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        FilmDTO filmDTO = gsonDeserialize
                .fromJson(mvcResult
                                .getResponse()
                                .getContentAsString(),
                        FilmDTO.class);

        FilmBriefDTO filmBriefDTO = new FilmBriefDTO(filmDTO);

        MvcResult mvcResult1 = this.mockMvc.perform(get("/users/{id}/favourites", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        FavouritesDTO favouritesDTO = gsonDeserialize
                .fromJson(mvcResult1
                                .getResponse()
                                .getContentAsString(),
                        FavouritesDTO.class);

        assertTrue(favouritesDTO
                .getFilms()
                .stream()
                .anyMatch(film -> film.checkIfContentEquals(filmBriefDTO)));

        this.mockMvc.perform(delete("/users/{id}/favourites", 1)
                .param("action", "film")
                .param("item", "3"))
                .andExpect(status().isOk());

        MvcResult mvcResult2 = this.mockMvc.perform(get("/users/{id}/favourites", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        FavouritesDTO favouritesDTO1 = gsonDeserialize
                .fromJson(mvcResult2
                                .getResponse()
                                .getContentAsString(),
                        FavouritesDTO.class);

        assertFalse(favouritesDTO1
                .getFilms()
                .stream()
                .anyMatch(film -> film.checkIfContentEquals(filmBriefDTO)));
    }

    @Test
    @WithUserDetails("admin")
    public void deleteFromWishlistTest() throws Exception {
        this.mockMvc.perform(post("/films/{id}", 1)
                .param("action", "wishlist"))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/films/{id}", 2)
                .param("action", "wishlist"))
                .andExpect(status().isOk());

        MvcResult mvcResult = this.mockMvc.perform(get("/films/{id}", 2)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        FilmDTO filmDTO = gsonDeserialize
                .fromJson(mvcResult
                                .getResponse()
                                .getContentAsString(),
                        FilmDTO.class);

        FilmBriefDTO filmBriefDTO = new FilmBriefDTO(filmDTO);

        this.mockMvc.perform(delete("/users/{id}/wishlist", 1)
                .param("item", "2"))
                .andExpect(status().isOk());

        MvcResult mvcResult1 = this.mockMvc.perform(get("/users/{id}/wishlist", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        WishlistDTO wishlistDTO = gsonDeserialize
                .fromJson(mvcResult1
                                .getResponse()
                                .getContentAsString(),
                        WishlistDTO.class);

        assertFalse(wishlistDTO.getFilms()
                .stream()
                .anyMatch(film -> film.checkIfContentEquals(filmBriefDTO)));
    }

}
