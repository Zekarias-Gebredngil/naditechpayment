package com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response;

import com.naditech.naditech.domain.model.AccountStatus;
import com.naditech.naditech.domain.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer Summary profile representation")
public class UserSummaryHttpResponse {

    @Schema(description = "Unique internal user identifier (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Customer email address", example = "customer@fintech.com")
    private String email;

    @Schema(description = "Customer phone number", example = "+12025550143")
    private String phoneNumber;

    @Schema(description = "Customer first name", example = "Alex")
    private String firstName;

    @Schema(description = "Customer last name", example = "Morgan")
    private String lastName;

    @Schema(description = "Customer full concatenated display name", example = "Alex Morgan")
    private String fullName;

    @Schema(description = "10-digit financial account number (Core Banking identifier)", example = "1049283741")
    private String accountNumber;

    @Schema(description = "Account currency code", example = "USD")
    private String currency;

    @Schema(description = "Assigned security role", example = "ROLE_USER")
    private Role role;

    @Schema(description = "Current account status", example = "ACTIVE")
    private AccountStatus accountStatus;

    @Schema(description = "Registration timestamp")
    private LocalDateTime createdAt;
}
