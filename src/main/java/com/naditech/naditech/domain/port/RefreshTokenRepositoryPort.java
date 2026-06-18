// Secondary output port for refresh token lifecycle management
package com.naditech.naditech.domain.port;

import com.naditech.naditech.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken save(RefreshToken refreshToken);
    void revokeAllUserTokens(UUID userId);
    void delete(RefreshToken refreshToken);
}
