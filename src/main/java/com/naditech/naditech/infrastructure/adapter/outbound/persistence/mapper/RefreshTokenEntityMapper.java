package com.naditech.naditech.infrastructure.adapter.outbound.persistence.mapper;

import com.naditech.naditech.domain.model.RefreshToken;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.entity.RefreshTokenJpaEntity;

public final class RefreshTokenEntityMapper {

    private RefreshTokenEntityMapper() {}

    public static RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return RefreshToken.builder()
                .id(entity.getId())
                .user(UserEntityMapper.toDomain(entity.getUser()))
                .token(entity.getToken())
                .expiryDate(entity.getExpiryDate())
                .revoked(entity.isRevoked())
                .replacedByToken(entity.getReplacedByToken())
                .clientType(entity.getClientType())
                .deviceId(entity.getDeviceId())
                .deviceName(entity.getDeviceName())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static RefreshTokenJpaEntity toJpaEntity(RefreshToken domain) {
        if (domain == null) {
            return null;
        }
        return RefreshTokenJpaEntity.builder()
                .id(domain.getId())
                .user(UserEntityMapper.toJpaEntity(domain.getUser()))
                .token(domain.getToken())
                .expiryDate(domain.getExpiryDate())
                .revoked(domain.isRevoked())
                .replacedByToken(domain.getReplacedByToken())
                .clientType(domain.getClientType())
                .deviceId(domain.getDeviceId())
                .deviceName(domain.getDeviceName())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
