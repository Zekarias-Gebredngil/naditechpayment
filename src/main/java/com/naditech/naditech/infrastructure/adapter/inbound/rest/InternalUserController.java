// Internal service-to-service REST adapter for Core Banking transactions
package com.naditech.naditech.infrastructure.adapter.inbound.rest;

import com.naditech.naditech.application.dto.result.UserProfileResult;
import com.naditech.naditech.application.port.in.CoreServiceUserQueryUseCase;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.ApiResponse;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.UserSummaryHttpResponse;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.mapper.RestDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Core Service Internal API", description = "Internal service-to-service endpoints for Core Banking and Transaction Services")
public class InternalUserController {

    private final CoreServiceUserQueryUseCase coreServiceUserQueryUseCase;

    @GetMapping("/by-account/{accountNumber}")
    @Operation(
            summary = "Fetch user details by account number (Core Service)",
            description = "Invoked by Core Banking Service during account lookups and transaction processing.",
            parameters = {
                    @Parameter(name = "X-Internal-Service-Key", in = ParameterIn.HEADER, description = "Secret key for internal microservice authentication", required = false)
            }
    )
    public ResponseEntity<ApiResponse<UserSummaryHttpResponse>> getByAccountNumber(@PathVariable String accountNumber) {
        log.info("Core service querying account number: {}", accountNumber);
        UserProfileResult result = coreServiceUserQueryUseCase.getByAccountNumber(accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Account profile retrieved", RestDtoMapper.toResponse(result)));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Fetch user details by User ID (Core Service)",
            description = "Invoked by Core Banking Service to retrieve user by UUID."
    )
    public ResponseEntity<ApiResponse<UserSummaryHttpResponse>> getById(@PathVariable UUID id) {
        UserProfileResult result = coreServiceUserQueryUseCase.getById(id);
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved", RestDtoMapper.toResponse(result)));
    }

    @GetMapping("/by-account/{accountNumber}/eligibility")
    @Operation(
            summary = "Verify account transaction eligibility (Core Service)",
            description = "Returns boolean indicating whether the account is ACTIVE and not locked/suspended."
    )
    public ResponseEntity<ApiResponse<Boolean>> isEligibleForTransaction(@PathVariable String accountNumber) {
        boolean eligible = coreServiceUserQueryUseCase.isAccountEligibleForTransaction(accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Eligibility checked", eligible));
    }
}
