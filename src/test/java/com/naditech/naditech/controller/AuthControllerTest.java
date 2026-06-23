// Unit test suite for AuthController using MockMvc and Mockito
package com.naditech.naditech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.naditech.naditech.application.dto.result.AuthResult;
import com.naditech.naditech.application.dto.result.UserProfileResult;
import com.naditech.naditech.application.port.in.LoginUserUseCase;
import com.naditech.naditech.application.port.in.LogoutUseCase;
import com.naditech.naditech.application.port.in.RefreshTokenUseCase;
import com.naditech.naditech.application.port.in.RegisterUserUseCase;
import com.naditech.naditech.application.port.in.ValidateTokenUseCase;
import com.naditech.naditech.domain.model.AccountStatus;
import com.naditech.naditech.domain.model.ClientType;
import com.naditech.naditech.domain.model.Role;
import com.naditech.naditech.infrastructure.adapter.inbound.exception.GlobalExceptionHandler;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.AuthController;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.request.LoginHttpRequest;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.request.RefreshTokenHttpRequest;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.request.RegisterHttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @Mock
    private LoginUserUseCase loginUserUseCase;

    @Mock
    private RefreshTokenUseCase refreshTokenUseCase;

    @Mock
    private LogoutUseCase logoutUseCase;

    @Mock
    private ValidateTokenUseCase validateTokenUseCase;

    @InjectMocks
    private AuthController authController;

    private AuthResult mockAuthResult;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        UserProfileResult userSummary = UserProfileResult.builder()
                .id(UUID.randomUUID())
                .email("alex.morgan@fintech.com")
                .phoneNumber("+12025550143")
                .firstName("Alex")
                .lastName("Morgan")
                .fullName("Alex Morgan")
                .accountNumber("1012345678")
                .currency("USD")
                .role(Role.ROLE_USER)
                .accountStatus(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        mockAuthResult = AuthResult.builder()
                .tokenType("Bearer")
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .expiresIn(86400000L)
                .user(userSummary)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should register user and return 201")
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterHttpRequest registerRequest = RegisterHttpRequest.builder()
                .email("alex.morgan@fintech.com")
                .phoneNumber("+12025550143")
                .firstName("Alex")
                .lastName("Morgan")
                .password("SecureP@ss2026")
                .currency("USD")
                .clientType(ClientType.MOBILE)
                .deviceId("device-12345")
                .deviceName("iPhone 15 Pro")
                .build();

        when(registerUserUseCase.register(any())).thenReturn(mockAuthResult);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-Type", "MOBILE")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("mock-refresh-token"))
                .andExpect(jsonPath("$.data.user.email").value("alex.morgan@fintech.com"))
                .andExpect(jsonPath("$.data.user.accountNumber").value("1012345678"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should return 400 when invalid email")
    void shouldRejectInvalidRegistrationPayload() throws Exception {
        RegisterHttpRequest invalidRequest = RegisterHttpRequest.builder()
                .email("invalid-email")
                .phoneNumber("123")
                .firstName("")
                .lastName("")
                .password("weak")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Should authenticate user and return 200")
    void shouldLoginSuccessfully() throws Exception {
        LoginHttpRequest loginRequest = LoginHttpRequest.builder()
                .identifier("alex.morgan@fintech.com")
                .password("SecureP@ss2026")
                .clientType(ClientType.WEB)
                .build();

        when(loginUserUseCase.login(any())).thenReturn(mockAuthResult);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.data.user.accountNumber").value("1012345678"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh-token - Should rotate tokens and return 200")
    void shouldRefreshTokenSuccessfully() throws Exception {
        RefreshTokenHttpRequest refreshRequest = RefreshTokenHttpRequest.builder()
                .refreshToken("mock-refresh-token")
                .build();

        when(refreshTokenUseCase.refreshToken(any())).thenReturn(mockAuthResult);

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"));
    }
}
