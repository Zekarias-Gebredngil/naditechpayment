// Secondary output port for audit log event persistence
package com.naditech.naditech.domain.port;

import com.naditech.naditech.domain.model.AuditLog;

public interface AuditLogRepositoryPort {
    AuditLog save(AuditLog auditLog);
}
