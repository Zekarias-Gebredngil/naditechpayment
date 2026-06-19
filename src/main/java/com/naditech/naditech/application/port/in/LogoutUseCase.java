// Primary input port for session termination and revocation
package com.naditech.naditech.application.port.in;

import com.naditech.naditech.domain.model.ClientType;

public interface LogoutUseCase {
    void logout(String refreshToken, ClientType clientType, String deviceId, String ipAddress, String userAgent);
}
