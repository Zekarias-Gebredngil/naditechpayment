// Primary input port for token rotation workflow
package com.naditech.naditech.application.port.in;

import com.naditech.naditech.application.dto.command.RefreshTokenCommand;
import com.naditech.naditech.application.dto.result.AuthResult;

public interface RefreshTokenUseCase {
    AuthResult refreshToken(RefreshTokenCommand command);
}
