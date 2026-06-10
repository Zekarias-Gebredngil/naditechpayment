package com.naditech.naditech.infrastructure.adapter.outbound.persistence.repository;

import com.naditech.naditech.infrastructure.adapter.outbound.persistence.entity.AuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataAuditLogRepository extends JpaRepository<AuditLogJpaEntity, UUID> {
}
