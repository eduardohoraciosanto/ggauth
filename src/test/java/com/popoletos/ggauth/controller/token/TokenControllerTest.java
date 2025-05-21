package com.popoletos.ggauth.controller.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.popoletos.ggauth.model.token.TokenSet;
import com.popoletos.ggauth.model.token.UserTokenSetRequest;
import com.popoletos.ggauth.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TokenControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_PLAYER_ID = "testPlayerId";

    private static final String TEST_APP_ID = "testAppId";

    @MockitoBean
    private TokenService tokenService;

    @Test
    void generateUserTokenSet() throws Exception {

        when(tokenService.generateUserTokenSet(TEST_PLAYER_ID))
                .thenReturn(TokenSet.builder()
                        .accessToken("someAccessToken")
                        .refreshToken("someRefreshToken")
                        .build());

        var tokenSetRequest =
                UserTokenSetRequest.builder().playerId(TEST_PLAYER_ID).build();

        var serializedTokenSet = objectMapper.writeValueAsString(tokenSetRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/token/generate/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedTokenSet))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("someAccessToken"));
    }

    /*
    @Test
    void generateApplicationTokenSet() throws Exception {

        when(tokenService.generateAppTokenSet(TEST_APP_ID))
                .thenReturn(TokenSet.builder()
                        .accessToken("someAccessToken")
                        .refreshToken("someRefreshToken")
                        .build());

        mockMvc.perform(MockMvcRequestBuilders.post("/token/generate/application")
                        .header("application-id", TEST_APP_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("someAccessToken"));
    }
    */

    @Test
    void validateToken_validToken() throws Exception {
        when(tokenService.validateToken("someToken")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/token/validate").header("Authorization", "Bearer someToken"))
                .andExpect(status().isOk());
    }

    @Test
    void validateToken_notValidToken() throws Exception {
        when(tokenService.validateToken("someToken")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/token/validate").header("Authorization", "Bearer someToken"))
                .andExpect(status().isUnauthorized());
    }
}