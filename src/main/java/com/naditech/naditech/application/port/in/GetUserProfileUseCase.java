// Primary input port for retrieving customer profile by id or email
package com.naditech.naditech.application.port.in;

import com.naditech.naditech.application.dto.result.UserProfileResult;

import java.util.UUID;

public interface GetUserProfileUseCase {
    UserProfileResult getById(UUID userId);
    UserProfileResult getByEmail(String email);
}
