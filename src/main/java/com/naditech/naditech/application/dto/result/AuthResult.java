package com.naditech.naditech.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResult {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UserProfileResult user;
}
