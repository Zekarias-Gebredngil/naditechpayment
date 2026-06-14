package com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.request;

import com.naditech.naditech.domain.model.ClientType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer Registration Request payload")
public class RegisterHttpRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Customer corporate or personal email address", example = "customer@fintech.com")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$", message = "Phone number must follow international E.164 format (e.g. +1234567890)")
    @Schema(description = "Customer mobile number in E.164 international standard format", example = "+12025550143")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    @Schema(description = "High-entropy password meeting Fintech security compliance standards", example = "StrongP@ssw0rd!")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Schema(description = "Customer first name", example = "Alex")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Schema(description = "Customer last name", example = "Morgan")
    private String lastName;

    @Schema(description = "Default currency for the customer account (ISO 4217 standard)", example = "USD", defaultValue = "USD")
    @Builder.Default
    private String currency = "USD";

    @Schema(description = "Client platform: WEB or MOBILE", example = "WEB", allowableValues = {"WEB", "MOBILE"})
    private ClientType clientType;

    @Schema(description = "Optional client device hardware ID", example = "device-uuid-98765")
    private String deviceId;

    @Schema(description = "Optional friendly device name", example = "iPhone 15 Pro")
    private String deviceName;
}
