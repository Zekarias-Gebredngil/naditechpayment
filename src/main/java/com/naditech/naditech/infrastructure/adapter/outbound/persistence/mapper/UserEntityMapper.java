package com.naditech.naditech.infrastructure.adapter.outbound.persistence.mapper;

import com.naditech.naditech.domain.model.User;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.entity.UserJpaEntity;

public final class UserEntityMapper {

    private UserEntityMapper() {}

    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .password(entity.getPassword())
                .accountNumber(entity.getAccountNumber())
                .currency(entity.getCurrency())
                .role(entity.getRole())
                .accountStatus(entity.getAccountStatus())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .lockoutExpiration(entity.getLockoutExpiration())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static UserJpaEntity toJpaEntity(User domain) {
        if (domain == null) {
            return null;
        }
        return UserJpaEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .phoneNumber(domain.getPhoneNumber())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .password(domain.getPassword())
                .accountNumber(domain.getAccountNumber())
                .currency(domain.getCurrency())
                .role(domain.getRole())
                .accountStatus(domain.getAccountStatus())
                .failedLoginAttempts(domain.getFailedLoginAttempts())
                .lockoutExpiration(domain.getLockoutExpiration())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
