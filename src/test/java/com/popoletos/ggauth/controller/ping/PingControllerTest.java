package com.popoletos.ggauth.controller.ping;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PingController.class)
@ActiveProfiles("test")
public class PingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPingEndpoint() throws Exception {
        String expectedVersion = "test";

        mockMvc.perform(MockMvcRequestBuilders.get("/ping").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(String.format("{\"version\":\"%s\"}", expectedVersion)));
    }
}
