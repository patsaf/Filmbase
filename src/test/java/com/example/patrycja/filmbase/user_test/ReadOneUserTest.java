package com.example.patrycja.filmbase.user_test;

import com.example.patrycja.filmbase.DTO.FilmDTO;
import com.example.patrycja.filmbase.DTO.UserDTO;
import com.example.patrycja.filmbase.template.FillBaseTemplate;
import com.example.patrycja.filmbase.template.LocalDateDeserializer;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ReadOneUserTest extends FillBaseTemplate {

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
        initUsers();
        postUsers();
    }

    private void postUsers() {
        List<String> jsons = new ArrayList<>();
        userRequests.forEach(userRequest -> jsons.add(gsonSerialize.toJson(userRequest)));
        jsons.forEach(json -> {
            try {
                mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = {"USER"})
    public void checkIfResponseContainsUserWithGivenId() throws Exception {
        for (int i = 1; i <= userRequests.size(); i++) {
            MvcResult mvcResult = this.mockMvc.perform(get("/users/{id}", i)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            UserDTO responseUser = gsonDeserialize.fromJson(mvcResult
                            .getResponse()
                            .getContentAsString(),
                    UserDTO.class
            );
            assertTrue(responseUser
                    .checkIfDataEquals(userRequests
                            .get(i - 1)
                            .getDTO()
                    ));
        }
    }

}
