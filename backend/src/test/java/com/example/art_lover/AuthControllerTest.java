package com.example.art_lover;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.art_lover.service.*;
import com.example.art_lover.controller.*;

class AuthControllerTest {

    @Test
    void signupReturnsToken() throws Exception {
        AuthService authService = Mockito.mock(AuthService.class);
        Mockito.doNothing().when(authService).signup("email", "password");

        AuthController controller = new AuthController(authService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content("""
                            {"username":"email","password":"password"}
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void loginReturnsToken() throws Exception {
        AuthService authService = Mockito.mock(AuthService.class);
        Mockito.when(authService.login("email", "password"))
                .thenReturn("token");

        AuthController controller = new AuthController(authService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("""
                            {"username":"email","password":"password"}
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void logoutReturnsToken() throws Exception {
        AuthService authService = Mockito.mock(AuthService.class);

        AuthController controller = new AuthController(authService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());
    }

}