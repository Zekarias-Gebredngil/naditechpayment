package com.naditech.naditech.application.port.in;

import com.naditech.naditech.application.dto.result.UserProfileResult;

import java.util.UUID;

public interface CoreServiceUserQueryUseCase {
    UserProfileResult getByAccountNumber(String accountNumber);
    UserProfileResult getById(UUID userId);
    boolean isAccountEligibleForTransaction(String accountNumber);
}
