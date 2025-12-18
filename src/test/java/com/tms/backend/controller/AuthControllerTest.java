package com.tms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.backend.dto.LoginRequest;
import com.tms.backend.repository.PasswordResetTokenRepository;
import com.tms.backend.repository.TwoFactorTokenRepository;
import com.tms.backend.repository.UserRepository;
import com.tms.backend.security.JwtUtils;
import com.tms.backend.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simple controller test
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private EmailService emailService;

    @MockBean
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @MockBean
    private TwoFactorTokenRepository twoFactorTokenRepository;

    @Test
    void login_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        // Mocking the authentication process
        Authentication authentication = mock(Authentication.class);
        // We need to mock the principal to avoid NullPointerException in AuthController
        com.tms.backend.security.UserDetailsImpl userDetails = mock(com.tms.backend.security.UserDetailsImpl.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(new com.tms.backend.model.User()));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn("test-jwt-token");

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrong@example.com");
        loginRequest.setPassword("wrongpass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
