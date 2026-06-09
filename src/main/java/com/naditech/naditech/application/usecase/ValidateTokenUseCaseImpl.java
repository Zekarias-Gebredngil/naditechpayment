package com.naditech.naditech.application.usecase;

import com.naditech.naditech.application.dto.result.TokenValidationResult;
import com.naditech.naditech.application.port.in.ValidateTokenUseCase;
import com.naditech.naditech.application.port.out.TokenProviderPort;
import com.naditech.naditech.domain.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateTokenUseCaseImpl implements ValidateTokenUseCase {

    private final TokenProviderPort tokenProvider;

    @Override
    public TokenValidationResult validate(String token) {
        if (token == null || token.isBlank()) {
            return TokenValidationResult.builder()
                    .valid(false)
                    .message("Token is null or empty")
                    .build();
        }

        String cleanToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();

        try {
            boolean isValid = tokenProvider.validateToken(cleanToken);
            if (!isValid) {
                return TokenValidationResult.builder()
                        .valid(false)
                        .message("Token is expired or signature is invalid")
                        .build();
            }

            String email = tokenProvider.extractUsername(cleanToken);
            UUID userId = tokenProvider.extractUserId(cleanToken);
            Role role = tokenProvider.extractRole(cleanToken);

            return TokenValidationResult.builder()
                    .valid(true)
                    .email(email)
                    .userId(userId)
                    .role(role)
                    .message("Token is valid")
                    .build();
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return TokenValidationResult.builder()
                    .valid(false)
                    .message("Validation error: " + e.getMessage())
                    .build();
        }
    }
}
