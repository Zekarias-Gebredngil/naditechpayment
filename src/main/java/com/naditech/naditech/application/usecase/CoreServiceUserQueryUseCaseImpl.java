package com.naditech.naditech.application.usecase;

import com.naditech.naditech.application.dto.result.UserProfileResult;
import com.naditech.naditech.application.mapper.UserProfileMapper;
import com.naditech.naditech.application.port.in.CoreServiceUserQueryUseCase;
import com.naditech.naditech.domain.exception.UserNotFoundException;
import com.naditech.naditech.domain.model.AccountStatus;
import com.naditech.naditech.domain.model.User;
import com.naditech.naditech.domain.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreServiceUserQueryUseCaseImpl implements CoreServiceUserQueryUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResult getByAccountNumber(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElseThrow(() -> new UserNotFoundException("User not found with account number: " + accountNumber));
        return UserProfileMapper.toResult(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResult getById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return UserProfileMapper.toResult(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountEligibleForTransaction(String accountNumber) {
        return userRepository.findByAccountNumber(accountNumber.trim())
                .map(user -> user.getAccountStatus() == AccountStatus.ACTIVE && !user.isAccountLocked())
                .orElse(false);
    }
}
