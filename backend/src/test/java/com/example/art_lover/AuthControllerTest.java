package com.example.art_lover;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.example.art_lover.service.*;
import com.example.art_lover.controller.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    JWTService jwtService;

    @Test
    void signupReturnsOk() throws Exception {

        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content("""
                            {"email":"email","password":"password"}
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    void loginReturnsOk() throws Exception {

        when(authService.login("email", "password"))
                .thenReturn("AUTH=token; Path=/; HttpOnly");

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("""
                            {"email":"email","password":"password"}
                        """))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andDo(print());
        ;
    }

    @Test
    void logoutReturnsOk() throws Exception {

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());
    }

}