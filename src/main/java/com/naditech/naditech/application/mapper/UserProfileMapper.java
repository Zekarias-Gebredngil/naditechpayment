package com.naditech.naditech.application.mapper;

import com.naditech.naditech.application.dto.result.UserProfileResult;
import com.naditech.naditech.domain.model.User;

public final class UserProfileMapper {

    private UserProfileMapper() {}

    public static UserProfileResult toResult(User user) {
        if (user == null) {
            return null;
        }
        return UserProfileResult.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .accountNumber(user.getAccountNumber())
                .currency(user.getCurrency())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
