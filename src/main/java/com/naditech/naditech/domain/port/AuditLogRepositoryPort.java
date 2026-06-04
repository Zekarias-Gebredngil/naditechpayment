package com.naditech.naditech.domain.port;

import com.naditech.naditech.domain.model.AuditLog;

public interface AuditLogRepositoryPort {
    AuditLog save(AuditLog auditLog);
}
