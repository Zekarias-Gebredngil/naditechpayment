package com.naditech.naditech.application.dto.result;

import com.naditech.naditech.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResult {
    private boolean valid;
    private UUID userId;
    private String email;
    private Role role;
    private String message;
}
