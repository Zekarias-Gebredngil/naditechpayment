package com.naditech.naditech.application.port.in;

import com.naditech.naditech.application.dto.result.TokenValidationResult;

public interface ValidateTokenUseCase {
    TokenValidationResult validate(String token);
}
