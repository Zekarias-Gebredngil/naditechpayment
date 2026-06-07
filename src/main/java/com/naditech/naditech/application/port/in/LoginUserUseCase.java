package com.naditech.naditech.application.port.in;

import com.naditech.naditech.application.dto.command.LoginUserCommand;
import com.naditech.naditech.application.dto.result.AuthResult;

public interface LoginUserUseCase {
    AuthResult login(LoginUserCommand command);
}
