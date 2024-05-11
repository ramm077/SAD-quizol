package com.valuelabs.livequiz.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReactControllerTests {

    private MockMvc mockMvc;

    @InjectMocks
    private ReactController reactController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reactController).build();
    }

    @Test
    void testForwardLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.forwardedUrl("/"));
    }

    @Test
    void testForwardForgotPassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/forgot_password"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.forwardedUrl("/"));
    }

    @Test
    void testForwardAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/somepath"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.forwardedUrl("/"));
    }

    // Repeat the similar test methods for other routes like /profile, /about, etc.

    @Test
    void testForwardQuizDisplay() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/quiz_display/somepath"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.forwardedUrl("/"));
    }
}