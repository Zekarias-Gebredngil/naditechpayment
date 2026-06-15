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
@Schema(description = "Customer Login Request payload")
public class LoginHttpRequest {

    @NotBlank(message = "Identifier (email or phone number) is required")
    @Schema(description = "Registered email or international phone number", example = "customer@fintech.com")
    private String identifier;

    @NotBlank(message = "Password is required")
    @Schema(description = "Customer secret password", example = "StrongP@ssw0rd!")
    private String password;

    @Schema(description = "Client platform: WEB or MOBILE", example = "WEB", allowableValues = {"WEB", "MOBILE"})
    private ClientType clientType;

    @Schema(description = "Unique client hardware/installation device ID", example = "device-uuid-98765")
    private String deviceId;

    @Schema(description = "Client device display name", example = "MacBook Pro M3")
    private String deviceName;
}
