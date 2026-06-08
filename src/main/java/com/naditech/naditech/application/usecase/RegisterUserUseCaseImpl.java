package com.naditech.naditech.application.usecase;

import com.naditech.naditech.application.dto.command.RegisterUserCommand;
import com.naditech.naditech.application.dto.result.AuthResult;
import com.naditech.naditech.application.mapper.UserProfileMapper;
import com.naditech.naditech.application.port.in.RegisterUserUseCase;
import com.naditech.naditech.application.port.out.AccountNumberGeneratorPort;
import com.naditech.naditech.application.port.out.PasswordEncoderPort;
import com.naditech.naditech.application.port.out.TokenProviderPort;
import com.naditech.naditech.domain.exception.UserAlreadyExistsException;
import com.naditech.naditech.domain.model.*;
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
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final AccountNumberGeneratorPort accountNumberGenerator;
    private final AuditLogRepositoryPort auditLogRepository;

    @Value("${application.security.jwt.refresh-token.expiration:604800000}")
    private long refreshTokenDurationMs;

    @Override
    @Transactional
    public AuthResult register(RegisterUserCommand command) {
        String cleanEmail = command.getEmail().trim().toLowerCase();
        String cleanPhone = command.getPhoneNumber().trim();

        if (userRepository.existsByEmail(cleanEmail)) {
            recordAudit(cleanEmail, "REGISTER", "FAILED", "Email already exists", command);
            throw new UserAlreadyExistsException("Email '" + cleanEmail + "' is already registered in Naditech");
        }

        if (userRepository.existsByPhoneNumber(cleanPhone)) {
            recordAudit(cleanEmail, "REGISTER", "FAILED", "Phone number already exists", command);
            throw new UserAlreadyExistsException("Phone number '" + cleanPhone + "' is already linked to an existing account");
        }

        String accountNumber = accountNumberGenerator.generate();
        ClientType clientType = command.getClientType() != null ? command.getClientType() : ClientType.WEB;

        User user = User.builder()
                .email(cleanEmail)
                .phoneNumber(cleanPhone)
                .firstName(command.getFirstName().trim())
                .lastName(command.getLastName().trim())
                .password(passwordEncoder.encode(command.getPassword()))
                .accountNumber(accountNumber)
                .currency(command.getCurrency() != null ? command.getCurrency().toUpperCase().trim() : "USD")
                .role(Role.ROLE_USER)
                .accountStatus(AccountStatus.ACTIVE)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(savedUser, clientType);
        RefreshToken refreshToken = createRefreshToken(savedUser, clientType, command.getDeviceId(), command.getDeviceName());

        recordAudit(savedUser.getEmail(), "REGISTER", "SUCCESS", "User registered with account " + accountNumber, command);

        return AuthResult.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(tokenProvider.getExpiration())
                .user(UserProfileMapper.toResult(savedUser))
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

    private void recordAudit(String identifier, String action, String status, String details, RegisterUserCommand command) {
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
