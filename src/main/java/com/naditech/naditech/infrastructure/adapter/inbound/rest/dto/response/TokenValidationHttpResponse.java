package com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response;

import com.naditech.naditech.domain.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token validation result for API Gateway / Edge Proxies")
public class TokenValidationHttpResponse {

    @Schema(description = "True if token signature is intact and not expired", example = "true")
    private boolean valid;

    @Schema(description = "Customer ID parsed from token claims", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID userId;

    @Schema(description = "Subject / Email parsed from token claims", example = "customer@fintech.com")
    private String email;

    @Schema(description = "Assigned security role", example = "ROLE_USER")
    private Role role;

    @Schema(description = "Status explanation message", example = "Token is valid")
    private String message;
}
