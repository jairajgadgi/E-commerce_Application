package com.example.ecommerce_application.controller;

import com.example.ecommerce_application.dto.AuthDtos.AuthResponse;
import com.example.ecommerce_application.dto.AuthDtos.LoginRequest;
import com.example.ecommerce_application.dto.AuthDtos.RegisterCustomerRequest;
import com.example.ecommerce_application.entity.Role;
import com.example.ecommerce_application.security.JwtService;
import com.example.ecommerce_application.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void loginShouldReturnToken() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(new AuthResponse("jwt-token", "admin", Role.ADMIN));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin", "admin123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void registerCustomerShouldReturnCreated() throws Exception {
        when(authService.registerCustomer(any(RegisterCustomerRequest.class))).thenReturn(new AuthResponse("jwt-token", "john@example.com", Role.CUSTOMER));

        mockMvc.perform(post("/api/v1/auth/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterCustomerRequest("John", "Doe", "john@example.com", "9999999999", "secret"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john@example.com"));
    }
}
