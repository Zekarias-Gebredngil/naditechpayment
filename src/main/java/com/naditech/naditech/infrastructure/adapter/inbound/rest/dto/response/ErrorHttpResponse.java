package com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard Error Response format conforming to RFC 7807")
public class ErrorHttpResponse {

    @Schema(description = "HTTP error status code", example = "400")
    private int status;

    @Schema(description = "HTTP error category name", example = "Bad Request")
    private String error;

    @Schema(description = "Human-readable detail message", example = "Email already registered in Naditech")
    private String message;

    @Schema(description = "API endpoint path where error occurred", example = "/api/v1/auth/register")
    private String path;

    @Schema(description = "Map of field-level validation errors (if applicable)")
    private Map<String, String> validationErrors;

    @Schema(description = "Timestamp when error occurred")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
