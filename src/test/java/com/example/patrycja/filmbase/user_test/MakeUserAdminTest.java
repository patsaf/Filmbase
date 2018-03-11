package com.example.patrycja.filmbase.user_test;

import com.example.patrycja.filmbase.request.SignUpRequest;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class MakeUserAdminTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private SignUpRequest signUpRequest;

    @Before
    public void init() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        signUpRequest = new SignUpRequest("user", "password", "user@test.mail");
        Gson gson = new Gson();
        this.mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(signUpRequest)));
    }

    @Test
    @WithUserDetails("admin")
    public void makeUserAdmin() throws Exception {
        this.mockMvc.perform(post("/users/{id}", 2))
                .andExpect(status().isOk());

        MvcResult mvcResult = this.mockMvc.perform(get("/users/{id}", 2)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String userDTO = mvcResult
                .getResponse()
                .getContentAsString();

        //Apparently GSON has problems parsing boolean (the return value is always false)
        //so had to assert it that way
        assertTrue(userDTO.contains("\"admin\":true"));
    }
}
