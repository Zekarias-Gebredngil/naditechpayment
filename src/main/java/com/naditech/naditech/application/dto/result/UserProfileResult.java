package com.naditech.naditech.application.dto.result;

import com.naditech.naditech.domain.model.AccountStatus;
import com.naditech.naditech.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResult {
    private UUID id;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String accountNumber;
    private String currency;
    private Role role;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
}
