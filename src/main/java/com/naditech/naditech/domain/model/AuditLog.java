// Immutable audit log event for compliance and security monitoring
package com.naditech.naditech.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    private UUID id;
    private String identifier;
    private String action;
    private String status;
    private String details;
    private ClientType clientType;
    private String ipAddress;
    private String userAgent;
    private String deviceId;
    private LocalDateTime createdAt;
}
