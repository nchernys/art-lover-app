package com.example.art_lover;

import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.art_lover.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthFlowIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  UserRepository userRepository;

  @AfterEach
  void cleanup() {
    userRepository.deleteByEmail("user-art-lover-888@test.com");
  }

  @Test
  void signup_then_login_with_same_credentials_succeeds() throws Exception {

    // signup
    mockMvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
                {
                  "email": "user-art-lover-888@test.com",
                  "password": "Secret123!"
                }
            """))
        .andExpect(status().isCreated());

    // login with same credentials
    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
                {
                  "email": "user-art-lover-888@test.com",
                  "password": "Secret123!"
                }
            """))
        .andExpect(status().isOk())
        .andExpect(header().exists(HttpHeaders.SET_COOKIE));
    ;
  }
}
