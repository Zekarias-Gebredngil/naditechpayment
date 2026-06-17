// Opaque cryptographic refresh token representation
package com.naditech.naditech.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private UUID id;
    private User user;
    private String token;
    private Instant expiryDate;
    private boolean revoked;
    private String replacedByToken;
    private ClientType clientType;
    private String deviceId;
    private String deviceName;
    private Instant createdAt;

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(Instant.now());
    }

    public void revoke(String replacementToken) {
        this.revoked = true;
        this.replacedByToken = replacementToken;
    }
}
