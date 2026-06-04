package com.naditech.naditech.application.port.out;

import com.naditech.naditech.domain.model.ClientType;
import com.naditech.naditech.domain.model.Role;
import com.naditech.naditech.domain.model.User;

import java.util.UUID;

public interface TokenProviderPort {
    String generateAccessToken(User user, ClientType clientType);
    boolean validateToken(String token);
    String extractUsername(String token);
    UUID extractUserId(String token);
    Role extractRole(String token);
    long getExpiration();
}
