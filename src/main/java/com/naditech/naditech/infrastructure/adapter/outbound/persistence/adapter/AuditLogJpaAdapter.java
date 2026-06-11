package com.naditech.naditech.infrastructure.adapter.outbound.persistence.adapter;

import com.naditech.naditech.domain.model.AuditLog;
import com.naditech.naditech.domain.port.AuditLogRepositoryPort;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.entity.AuditLogJpaEntity;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.mapper.AuditLogEntityMapper;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.repository.SpringDataAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogJpaAdapter implements AuditLogRepositoryPort {

    private final SpringDataAuditLogRepository springDataAuditLogRepository;

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogJpaEntity jpaEntity = AuditLogEntityMapper.toJpaEntity(auditLog);
        AuditLogJpaEntity saved = springDataAuditLogRepository.save(jpaEntity);
        return AuditLogEntityMapper.toDomain(saved);
    }
}
