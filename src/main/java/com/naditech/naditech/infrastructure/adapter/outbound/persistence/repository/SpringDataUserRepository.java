package com.naditech.naditech.infrastructure.adapter.outbound.persistence.repository;

import com.naditech.naditech.infrastructure.adapter.outbound.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);

    Optional<UserJpaEntity> findByPhoneNumber(String phoneNumber);

    Optional<UserJpaEntity> findByAccountNumber(String accountNumber);

    @Query("SELECT u FROM UserJpaEntity u WHERE LOWER(u.email) = LOWER(:identifier) OR u.phoneNumber = :identifier")
    Optional<UserJpaEntity> findByEmailOrPhone(@Param("identifier") String identifier);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByAccountNumber(String accountNumber);
}
