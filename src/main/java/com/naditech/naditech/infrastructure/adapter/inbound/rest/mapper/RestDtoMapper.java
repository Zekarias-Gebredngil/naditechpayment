package com.naditech.naditech.infrastructure.adapter.inbound.rest.mapper;

import com.naditech.naditech.application.dto.command.LoginUserCommand;
import com.naditech.naditech.application.dto.command.RefreshTokenCommand;
import com.naditech.naditech.application.dto.command.RegisterUserCommand;
import com.naditech.naditech.application.dto.result.AuthResult;
import com.naditech.naditech.application.dto.result.TokenValidationResult;
import com.naditech.naditech.application.dto.result.UserProfileResult;
import com.naditech.naditech.domain.model.ClientType;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.request.LoginHttpRequest;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.request.RefreshTokenHttpRequest;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.request.RegisterHttpRequest;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.AuthHttpResponse;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.TokenValidationHttpResponse;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.UserSummaryHttpResponse;
import jakarta.servlet.http.HttpServletRequest;

public final class RestDtoMapper {

    private RestDtoMapper() {}

    public static RegisterUserCommand toCommand(RegisterHttpRequest request, HttpServletRequest httpRequest) {
        ClientType clientType = resolveClientType(httpRequest, request.getClientType());
        return RegisterUserCommand.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .currency(request.getCurrency())
                .clientType(clientType)
                .deviceId(request.getDeviceId())
                .deviceName(request.getDeviceName())
                .ipAddress(extractClientIp(httpRequest))
                .userAgent(httpRequest.getHeader("User-Agent"))
                .build();
    }

    public static LoginUserCommand toCommand(LoginHttpRequest request, HttpServletRequest httpRequest) {
        ClientType clientType = resolveClientType(httpRequest, request.getClientType());
        return LoginUserCommand.builder()
                .identifier(request.getIdentifier())
                .password(request.getPassword())
                .clientType(clientType)
                .deviceId(request.getDeviceId())
                .deviceName(request.getDeviceName())
                .ipAddress(extractClientIp(httpRequest))
                .userAgent(httpRequest.getHeader("User-Agent"))
                .build();
    }

    public static RefreshTokenCommand toCommand(RefreshTokenHttpRequest request, HttpServletRequest httpRequest) {
        ClientType clientType = resolveClientType(httpRequest, request.getClientType());
        return RefreshTokenCommand.builder()
                .refreshToken(request.getRefreshToken())
                .clientType(clientType)
                .deviceId(request.getDeviceId())
                .deviceName(request.getDeviceName())
                .ipAddress(extractClientIp(httpRequest))
                .userAgent(httpRequest.getHeader("User-Agent"))
                .build();
    }

    public static AuthHttpResponse toResponse(AuthResult result) {
        if (result == null) {
            return null;
        }
        return AuthHttpResponse.builder()
                .tokenType(result.getTokenType())
                .accessToken(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .expiresIn(result.getExpiresIn())
                .user(toResponse(result.getUser()))
                .build();
    }

    public static UserSummaryHttpResponse toResponse(UserProfileResult result) {
        if (result == null) {
            return null;
        }
        return UserSummaryHttpResponse.builder()
                .id(result.getId())
                .email(result.getEmail())
                .phoneNumber(result.getPhoneNumber())
                .firstName(result.getFirstName())
                .lastName(result.getLastName())
                .fullName(result.getFullName())
                .accountNumber(result.getAccountNumber())
                .currency(result.getCurrency())
                .role(result.getRole())
                .accountStatus(result.getAccountStatus())
                .createdAt(result.getCreatedAt())
                .build();
    }

    public static TokenValidationHttpResponse toResponse(TokenValidationResult result) {
        if (result == null) {
            return null;
        }
        return TokenValidationHttpResponse.builder()
                .valid(result.isValid())
                .userId(result.getUserId())
                .email(result.getEmail())
                .role(result.getRole())
                .message(result.getMessage())
                .build();
    }

    public static ClientType resolveClientType(HttpServletRequest request, ClientType bodyClientType) {
        if (bodyClientType != null) {
            return bodyClientType;
        }
        String headerClientType = request.getHeader("X-Client-Type");
        if (headerClientType != null && !headerClientType.isBlank()) {
            try {
                return ClientType.valueOf(headerClientType.toUpperCase().trim());
            } catch (IllegalArgumentException ignored) {
            }
        }
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            String lower = userAgent.toLowerCase();
            if (lower.contains("mobile") || lower.contains("android") || lower.contains("iphone") || lower.contains("dart")) {
                return ClientType.MOBILE;
            }
        }
        return ClientType.WEB;
    }

    public static String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
