package com.naditech.naditech.application.usecase;

import com.naditech.naditech.application.port.in.LogoutUseCase;
import com.naditech.naditech.domain.model.AuditLog;
import com.naditech.naditech.domain.model.ClientType;
import com.naditech.naditech.domain.port.AuditLogRepositoryPort;
import com.naditech.naditech.domain.port.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final AuditLogRepositoryPort auditLogRepository;

    @Override
    @Transactional
    public void logout(String refreshToken, ClientType clientType, String deviceId, String ipAddress, String userAgent) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);

                AuditLog audit = AuditLog.builder()
                        .identifier(token.getUser().getEmail())
                        .action("LOGOUT")
                        .status("SUCCESS")
                        .details("Session revoked")
                        .clientType(clientType)
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .deviceId(deviceId)
                        .createdAt(LocalDateTime.now())
                        .build();
                auditLogRepository.save(audit);
            });
        }
    }
}
