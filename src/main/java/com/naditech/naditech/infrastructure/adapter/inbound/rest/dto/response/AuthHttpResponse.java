package com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication & Onboarding Token response payload")
public class AuthHttpResponse {

    @Schema(description = "HTTP Authentication Scheme", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "JWT Access Token for Bearer authentication headers", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Opaque cryptographically secure Refresh Token", example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d-uuid")
    private String refreshToken;

    @Schema(description = "Access token validity lifespan in milliseconds", example = "86400000")
    private long expiresIn;

    @Schema(description = "Customer summary information")
    private UserSummaryHttpResponse user;
}
