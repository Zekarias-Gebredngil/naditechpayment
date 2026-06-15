package com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.request;

import com.naditech.naditech.domain.model.ClientType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Refresh Token Request payload")
public class RefreshTokenHttpRequest {

    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Opaque refresh token issued during login or registration", example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d-uuid")
    private String refreshToken;

    @Schema(description = "Client platform: WEB or MOBILE", example = "WEB")
    private ClientType clientType;

    @Schema(description = "Unique client hardware/installation device ID", example = "device-uuid-98765")
    private String deviceId;

    @Schema(description = "Client device display name", example = "iPhone 15 Pro")
    private String deviceName;
}
