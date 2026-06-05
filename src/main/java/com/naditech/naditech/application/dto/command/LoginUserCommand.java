package com.naditech.naditech.application.dto.command;

import com.naditech.naditech.domain.model.ClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserCommand {
    private String identifier;
    private String password;
    private ClientType clientType;
    private String deviceId;
    private String deviceName;
    private String ipAddress;
    private String userAgent;
}
