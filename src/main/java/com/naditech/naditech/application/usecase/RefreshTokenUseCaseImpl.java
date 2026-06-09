package com.naditech.naditech.application.usecase;

import com.naditech.naditech.application.dto.command.RefreshTokenCommand;
import com.naditech.naditech.application.dto.result.AuthResult;
import com.naditech.naditech.application.mapper.UserProfileMapper;
import com.naditech.naditech.application.port.in.RefreshTokenUseCase;
import com.naditech.naditech.application.port.out.TokenProviderPort;
import com.naditech.naditech.domain.exception.InvalidTokenException;
import com.naditech.naditech.domain.model.AuditLog;
import com.naditech.naditech.domain.model.ClientType;
import com.naditech.naditech.domain.model.RefreshToken;
import com.naditech.naditech.domain.model.User;
import com.naditech.naditech.domain.port.AuditLogRepositoryPort;
import com.naditech.naditech.domain.port.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final TokenProviderPort tokenProvider;
    private final AuditLogRepositoryPort auditLogRepository;

    @Value("${application.security.jwt.refresh-token.expiration:604800000}")
    private long refreshTokenDurationMs;

    @Override
    @Transactional
    public AuthResult refreshToken(RefreshTokenCommand command) {
        String tokenStr = command.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new InvalidTokenException("Refresh token does not exist"));

        if (refreshToken.isRevoked()) {
            recordAudit(refreshToken.getUser().getEmail(), "REFRESH_TOKEN", "FAILED", "Revoked token reuse detected", command);
            refreshTokenRepository.revokeAllUserTokens(refreshToken.getUser().getId());
            throw new InvalidTokenException("Refresh token has been revoked. All sessions invalidated for security.");
        }

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            recordAudit(refreshToken.getUser().getEmail(), "REFRESH_TOKEN", "FAILED", "Expired token used", command);
            throw new InvalidTokenException("Refresh token has expired. Please login again.");
        }

        User user = refreshToken.getUser();
        ClientType clientType = command.getClientType() != null ? command.getClientType() : refreshToken.getClientType();

        // Token rotation: revoke current token and create new one
        String newRefreshTokenStr = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        refreshToken.revoke(newRefreshTokenStr);
        refreshTokenRepository.save(refreshToken);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .token(newRefreshTokenStr)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .clientType(clientType)
                .deviceId(command.getDeviceId() != null ? command.getDeviceId() : refreshToken.getDeviceId())
                .deviceName(command.getDeviceName() != null ? command.getDeviceName() : refreshToken.getDeviceName())
                .createdAt(Instant.now())
                .build();
        refreshTokenRepository.save(newRefreshToken);

        String newAccessToken = tokenProvider.generateAccessToken(user, clientType);

        recordAudit(user.getEmail(), "REFRESH_TOKEN", "SUCCESS", "Rotated refresh token", command);

        return AuthResult.builder()
                .tokenType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .expiresIn(tokenProvider.getExpiration())
                .user(UserProfileMapper.toResult(user))
                .build();
    }

    private void recordAudit(String identifier, String action, String status, String details, RefreshTokenCommand command) {
        AuditLog audit = AuditLog.builder()
                .identifier(identifier)
                .action(action)
                .status(status)
                .details(details)
                .clientType(command.getClientType())
                .ipAddress(command.getIpAddress())
                .userAgent(command.getUserAgent())
                .deviceId(command.getDeviceId())
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(audit);
    }
}
