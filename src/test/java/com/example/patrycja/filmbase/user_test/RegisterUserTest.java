package com.example.patrycja.filmbase.user_test;

import com.example.patrycja.filmbase.request.SignUpRequest;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class RegisterUserTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private SignUpRequest signUpRequest;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        signUpRequest = new SignUpRequest("user", "password", "user@test.mail");
    }

    @Test
    public void registerValidUser() throws Exception {
        Gson gson = new Gson();
        this.mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(signUpRequest)))
                .andExpect(status().isCreated());
    }
}
