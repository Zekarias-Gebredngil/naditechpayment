package com.naditech.naditech.infrastructure.adapter.outbound.persistence.mapper;

import com.naditech.naditech.domain.model.AuditLog;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.entity.AuditLogJpaEntity;

public final class AuditLogEntityMapper {

    private AuditLogEntityMapper() {}

    public static AuditLog toDomain(AuditLogJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return AuditLog.builder()
                .id(entity.getId())
                .identifier(entity.getIdentifier())
                .action(entity.getAction())
                .status(entity.getStatus())
                .details(entity.getDetails())
                .clientType(entity.getClientType())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .deviceId(entity.getDeviceId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static AuditLogJpaEntity toJpaEntity(AuditLog domain) {
        if (domain == null) {
            return null;
        }
        return AuditLogJpaEntity.builder()
                .id(domain.getId())
                .identifier(domain.getIdentifier())
                .action(domain.getAction())
                .status(domain.getStatus())
                .details(domain.getDetails())
                .clientType(domain.getClientType())
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .deviceId(domain.getDeviceId())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
