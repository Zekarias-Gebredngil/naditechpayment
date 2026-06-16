package com.naditech.naditech.infrastructure.adapter.inbound.rest;

import com.naditech.naditech.application.dto.result.AuthResult;
import com.naditech.naditech.application.dto.result.TokenValidationResult;
import com.naditech.naditech.application.port.in.LoginUserUseCase;
import com.naditech.naditech.application.port.in.LogoutUseCase;
import com.naditech.naditech.application.port.in.RefreshTokenUseCase;
import com.naditech.naditech.application.port.in.RegisterUserUseCase;
import com.naditech.naditech.application.port.in.ValidateTokenUseCase;
import com.naditech.naditech.domain.model.ClientType;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.request.LoginHttpRequest;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.request.RefreshTokenHttpRequest;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.request.RegisterHttpRequest;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.ApiResponse;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.AuthHttpResponse;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.ErrorHttpResponse;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.TokenValidationHttpResponse;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.mapper.RestDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication & Onboarding", description = "Endpoints for customer registration, authentication, token rotation, and API Gateway token validation")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final ValidateTokenUseCase validateTokenUseCase;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new financial customer",
            description = "Creates a new user profile, assigns an account number, issues JWT access and refresh tokens.",
            parameters = {
                    @Parameter(name = "X-Client-Type", in = ParameterIn.HEADER, description = "Client platform: WEB or MOBILE", schema = @Schema(type = "string", allowableValues = {"WEB", "MOBILE"}))
            }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Customer registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthHttpResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error in request payload",
                    content = @Content(schema = @Schema(implementation = ErrorHttpResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email or phone number already in use",
                    content = @Content(schema = @Schema(implementation = ErrorHttpResponse.class)))
    })
    public ResponseEntity<ApiResponse<AuthHttpResponse>> register(
            @Valid @RequestBody RegisterHttpRequest request,
            HttpServletRequest httpRequest) {
        log.info("Processing customer registration for email: {}", request.getEmail());
        AuthResult result = registerUserUseCase.register(RestDtoMapper.toCommand(request, httpRequest));
        return new ResponseEntity<>(
                ApiResponse.success("Customer registered successfully", RestDtoMapper.toResponse(result)),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate customer (Web & Mobile)",
            description = "Validates credentials using email or phone number. Returns JWT access token and refresh token.",
            parameters = {
                    @Parameter(name = "X-Client-Type", in = ParameterIn.HEADER, description = "Client platform: WEB or MOBILE", schema = @Schema(type = "string", allowableValues = {"WEB", "MOBILE"})),
                    @Parameter(name = "X-Device-Id", in = ParameterIn.HEADER, description = "Optional unique hardware device identifier", schema = @Schema(type = "string"))
            }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = AuthHttpResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorHttpResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorHttpResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "423", description = "Account locked due to consecutive failed attempts",
                    content = @Content(schema = @Schema(implementation = ErrorHttpResponse.class)))
    })
    public ResponseEntity<ApiResponse<AuthHttpResponse>> login(
            @Valid @RequestBody LoginHttpRequest request,
            HttpServletRequest httpRequest) {
        log.info("Processing login attempt for identifier: {}", request.getIdentifier());
        AuthResult result = loginUserUseCase.login(RestDtoMapper.toCommand(request, httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Login successful", RestDtoMapper.toResponse(result)));
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Refresh expired access token",
            description = "Token rotation: validates and revokes current refresh token, issuing a new access token and fresh refresh token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthHttpResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token expired or revoked",
                    content = @Content(schema = @Schema(implementation = ErrorHttpResponse.class)))
    })
    public ResponseEntity<ApiResponse<AuthHttpResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenHttpRequest request,
            HttpServletRequest httpRequest) {
        AuthResult result = refreshTokenUseCase.refreshToken(RestDtoMapper.toCommand(request, httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", RestDtoMapper.toResponse(result)));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout customer and revoke session",
            description = "Revokes the active refresh token session."
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody(required = false) RefreshTokenHttpRequest request,
            HttpServletRequest httpRequest) {
        String token = request != null ? request.getRefreshToken() : null;
        ClientType clientType = RestDtoMapper.resolveClientType(httpRequest, request != null ? request.getClientType() : null);
        String deviceId = request != null ? request.getDeviceId() : null;
        logoutUseCase.logout(token, clientType, deviceId, RestDtoMapper.extractClientIp(httpRequest), httpRequest.getHeader("User-Agent"));
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @PostMapping("/validate")
    @Operation(
            summary = "Validate Token (API Gateway integration)",
            description = "Endpoint invoked by the API Gateway to validate an incoming JWT Bearer token and extract user details and roles."
    )
    public ResponseEntity<ApiResponse<TokenValidationHttpResponse>> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "token", required = false) String queryToken) {
        String tokenToVerify = (authHeader != null && !authHeader.isBlank()) ? authHeader : queryToken;
        TokenValidationResult result = validateTokenUseCase.validate(tokenToVerify);
        return ResponseEntity.ok(ApiResponse.success("Token evaluated", RestDtoMapper.toResponse(result)));
    }
}
