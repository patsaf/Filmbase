package com.example.patrycja.filmbase.user_test;

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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class MoveFromWishlistToFavouriteTest extends FillBaseTemplate {

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
        this.mockMvc.perform(post("/films/{id}", 4) //adds film to wishlist
                .param("action", "wishlist"))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/films/{id}", 4) //moves the film from wishlist to favourites
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

        //assert film was removed from wishlist
        assertFalse(wishlistDTO.getFilms().contains(filmBriefDTO));
    }

    @Test
    @WithUserDetails("admin")
    public void wishlistFavouriteMoveViaUserProfileTest() throws Exception {
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

}
