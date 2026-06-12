package com.naditech.naditech.infrastructure.adapter.outbound.security;

import com.naditech.naditech.application.port.out.AccountNumberGeneratorPort;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.repository.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class RandomAccountNumberGeneratorAdapter implements AccountNumberGeneratorPort {

    private final SpringDataUserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    private static final String BRANCH_CODE = "10"; // Naditech Digital Branch Prefix

    @Override
    public String generate() {
        String accountNumber;
        do {
            long randomDigits = 10000000L + secureRandom.nextLong(90000000L);
            accountNumber = BRANCH_CODE + randomDigits;
        } while (userRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}
