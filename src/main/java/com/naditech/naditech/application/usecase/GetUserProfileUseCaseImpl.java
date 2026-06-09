package com.naditech.naditech.application.usecase;

import com.naditech.naditech.application.dto.result.UserProfileResult;
import com.naditech.naditech.application.mapper.UserProfileMapper;
import com.naditech.naditech.application.port.in.GetUserProfileUseCase;
import com.naditech.naditech.domain.exception.UserNotFoundException;
import com.naditech.naditech.domain.model.User;
import com.naditech.naditech.domain.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserProfileUseCaseImpl implements GetUserProfileUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResult getById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return UserProfileMapper.toResult(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResult getByEmail(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return UserProfileMapper.toResult(user);
    }
}
