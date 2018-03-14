package com.example.patrycja.filmbase.error_test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class MissingParamTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "test", password = "test", roles = {"USER"})
    public void rejectActionWithMissingParam() throws Exception {
        this.mockMvc.perform(post("/films/{id}", 1)
                .param("action", "rate"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/actors/{id}", 1)
                .param("action", "rate"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/directors/{id}", 1)
                .param("action", "rate"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/actors/{id}", 2)
                .param("action", "update"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/directors/{id}", 2)
                .param("action", "update"))
                .andExpect(status().isBadRequest());
    }
}
