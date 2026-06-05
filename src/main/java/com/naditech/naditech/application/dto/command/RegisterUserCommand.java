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
public class RegisterUserCommand {
    private String email;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    private String currency;
    private ClientType clientType;
    private String deviceId;
    private String deviceName;
    private String ipAddress;
    private String userAgent;
}
