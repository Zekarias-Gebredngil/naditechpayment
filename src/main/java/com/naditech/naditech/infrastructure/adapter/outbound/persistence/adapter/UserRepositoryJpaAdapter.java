// Outbound persistence adapter implementing domain UserRepositoryPort
package com.naditech.naditech.infrastructure.adapter.outbound.persistence.adapter;

import com.naditech.naditech.domain.model.User;
import com.naditech.naditech.domain.port.UserRepositoryPort;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.entity.UserJpaEntity;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.mapper.UserEntityMapper;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.repository.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryJpaAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id)
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmailIgnoreCase(email)
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return springDataUserRepository.findByPhoneNumber(phoneNumber)
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailOrPhone(String identifier) {
        return springDataUserRepository.findByEmailOrPhone(identifier)
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByAccountNumber(String accountNumber) {
        return springDataUserRepository.findByAccountNumber(accountNumber)
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return springDataUserRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = UserEntityMapper.toJpaEntity(user);
        UserJpaEntity saved = springDataUserRepository.save(jpaEntity);
        return UserEntityMapper.toDomain(saved);
    }
}
