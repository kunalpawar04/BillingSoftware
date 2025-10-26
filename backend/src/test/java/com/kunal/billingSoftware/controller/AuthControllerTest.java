package com.kunal.billingSoftware.controller;

import com.kunal.billingSoftware.exceptions.InvalidCredentialsException;
import com.kunal.billingSoftware.io.AuthRequest;
import com.kunal.billingSoftware.service.UserService;
import com.kunal.billingSoftware.service.impl.AppUserDetailsService;
import com.kunal.billingSoftware.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserService userService;

    @Test
    void testLogin_ShouldReturnAuthResponse_WhenCredentialsAreValid() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        AuthRequest request = new AuthRequest(email, password);
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authenticationMock = mock(Authentication.class);

        when(appUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("fake-jwt-token");
        when(userService.getUserRole(email)).thenReturn("USER");

        // Stub authenticationManager.authenticate() so it doesn't throw
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);

        // Act + Assert
        mockMvc.perform(post("/login")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                        {
                          "email": "test@example.com",
                          "password": "password123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.role").value("USER"));

        // Verify
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken(userDetails);
    }

    @Test
    void testLogin_ShouldReturnBadRequest_WhenCredentialsAreInvalid() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "wrongPassword";
        AuthRequest request = new AuthRequest(email, password);

        // Simulate authentication failure
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act + Assert
        mockMvc.perform(post("/login")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                            {
                              "email": "test@example.com",
                              "password": "wrongPassword"
                            }
                            """))
                .andExpect(status().isBadRequest()) // Expect 400 for bad credentials
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof InvalidCredentialsException))
                .andExpect(result ->
                        assertEquals("Invalid email or password",
                                result.getResolvedException().getMessage()));

        // Verify
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testEncodePassword_ShouldEncodePassword() throws Exception{
        String rawPassword = "Pass@123";
        String encodedPassword = "##$TBRJUhrqiyg666718";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        mockMvc.perform(post("/encode")
                        .contentType(MediaType.APPLICATION_JSON) // pass the MediaType directly
                        .content("""
                            {
                            "password": "Pass@123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string(encodedPassword));

        verify(passwordEncoder, times(1)).encode(rawPassword);
    }
}