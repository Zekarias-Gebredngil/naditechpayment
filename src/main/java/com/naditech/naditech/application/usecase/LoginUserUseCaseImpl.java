// Implementation of multi-platform authentication with account lockout policy
package com.naditech.naditech.application.usecase;

import com.naditech.naditech.application.dto.command.LoginUserCommand;
import com.naditech.naditech.application.dto.result.AuthResult;
import com.naditech.naditech.application.mapper.UserProfileMapper;
import com.naditech.naditech.application.port.in.LoginUserUseCase;
import com.naditech.naditech.application.port.out.PasswordEncoderPort;
import com.naditech.naditech.application.port.out.TokenProviderPort;
import com.naditech.naditech.domain.exception.AccountLockedException;
import com.naditech.naditech.domain.exception.AccountSuspendedException;
import com.naditech.naditech.domain.exception.InvalidCredentialsException;
import com.naditech.naditech.domain.model.AuditLog;
import com.naditech.naditech.domain.model.ClientType;
import com.naditech.naditech.domain.model.RefreshToken;
import com.naditech.naditech.domain.model.User;
import com.naditech.naditech.domain.port.AuditLogRepositoryPort;
import com.naditech.naditech.domain.port.RefreshTokenRepositoryPort;
import com.naditech.naditech.domain.port.UserRepositoryPort;
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
public class LoginUserUseCaseImpl implements LoginUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final AuditLogRepositoryPort auditLogRepository;

    @Value("${application.security.jwt.refresh-token.expiration:604800000}")
    private long refreshTokenDurationMs;

    @Value("${application.security.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${application.security.lockout-duration-minutes:15}")
    private int lockoutDurationMinutes;

    @Override
    @Transactional
    public AuthResult login(LoginUserCommand command) {
        String identifier = command.getIdentifier().trim();
        ClientType clientType = command.getClientType() != null ? command.getClientType() : ClientType.WEB;

        User user = userRepository.findByEmailOrPhone(identifier)
                .orElseThrow(() -> {
                    recordAudit(identifier, "LOGIN", "FAILED", "User not found", command);
                    return new InvalidCredentialsException("Invalid email/phone or password");
                });

        if (user.isAccountLocked()) {
            recordAudit(user.getEmail(), "LOGIN", "FAILED", "Account locked", command);
            throw new AccountLockedException("Your account is temporarily locked due to multiple failed login attempts. Please try again after " + user.getLockoutExpiration());
        }

        if (user.isSuspended()) {
            recordAudit(user.getEmail(), "LOGIN", "FAILED", "Account suspended", command);
            throw new AccountSuspendedException("Your account has been suspended. Please contact customer support.");
        }

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            user.recordFailedLogin(maxFailedAttempts, lockoutDurationMinutes);
            userRepository.save(user);

            if (user.isAccountLocked()) {
                recordAudit(user.getEmail(), "LOGIN", "FAILED", "Account locked after " + maxFailedAttempts + " attempts", command);
                throw new AccountLockedException("Account locked due to " + maxFailedAttempts + " consecutive failed attempts. Try again in " + lockoutDurationMinutes + " minutes.");
            }

            int remaining = maxFailedAttempts - user.getFailedLoginAttempts();
            recordAudit(user.getEmail(), "LOGIN", "FAILED", "Invalid password (" + remaining + " remaining)", command);
            throw new InvalidCredentialsException("Invalid email/phone or password. " + remaining + " attempt(s) remaining.");
        }

        user.recordSuccessfulLogin();
        userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(user, clientType);
        RefreshToken refreshToken = createRefreshToken(user, clientType, command.getDeviceId(), command.getDeviceName());

        recordAudit(user.getEmail(), "LOGIN", "SUCCESS", "Logged in via " + clientType, command);

        return AuthResult.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(tokenProvider.getExpiration())
                .user(UserProfileMapper.toResult(user))
                .build();
    }

    private RefreshToken createRefreshToken(User user, ClientType clientType, String deviceId, String deviceName) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .clientType(clientType)
                .deviceId(deviceId)
                .deviceName(deviceName)
                .createdAt(Instant.now())
                .build();
        return refreshTokenRepository.save(token);
    }

    private void recordAudit(String identifier, String action, String status, String details, LoginUserCommand command) {
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
