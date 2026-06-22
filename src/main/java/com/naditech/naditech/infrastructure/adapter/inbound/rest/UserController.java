// REST adapter for customer profile retrieval and account details
package com.naditech.naditech.infrastructure.adapter.inbound.rest;

import com.naditech.naditech.application.dto.result.UserProfileResult;
import com.naditech.naditech.application.port.in.GetUserProfileUseCase;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.ApiResponse;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.UserSummaryHttpResponse;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.mapper.RestDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Profile Management", description = "Endpoints for retrieving and managing customer profiles")
public class UserController {

    private final GetUserProfileUseCase getUserProfileUseCase;

    @GetMapping("/me")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get current authenticated customer profile",
            description = "Retrieves profile and account summary for the currently authenticated user (via JWT or Gateway headers)."
    )
    public ResponseEntity<ApiResponse<UserSummaryHttpResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-User-Email", required = false) String gatewayEmail) {
        String email = (userDetails != null) ? userDetails.getUsername() : gatewayEmail;
        UserProfileResult result = getUserProfileUseCase.getByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", RestDtoMapper.toResponse(result)));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get customer profile by ID",
            description = "Retrieves profile details by UUID."
    )
    public ResponseEntity<ApiResponse<UserSummaryHttpResponse>> getById(@PathVariable UUID id) {
        UserProfileResult result = getUserProfileUseCase.getById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", RestDtoMapper.toResponse(result)));
    }
}
