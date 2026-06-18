// Primary input port for customer registration use case
package com.naditech.naditech.application.port.in;

import com.naditech.naditech.application.dto.command.RegisterUserCommand;
import com.naditech.naditech.application.dto.result.AuthResult;

public interface RegisterUserUseCase {
    AuthResult register(RegisterUserCommand command);
}
