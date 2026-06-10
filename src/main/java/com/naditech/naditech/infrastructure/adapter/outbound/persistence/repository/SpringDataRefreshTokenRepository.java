package com.naditech.naditech.infrastructure.adapter.outbound.persistence.repository;

import com.naditech.naditech.infrastructure.adapter.outbound.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    @Query("SELECT r FROM RefreshTokenJpaEntity r JOIN FETCH r.user WHERE r.token = :token")
    Optional<RefreshTokenJpaEntity> findByTokenWithUser(@Param("token") String token);

    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revoked = true WHERE r.user.id = :userId AND r.revoked = false")
    void revokeAllByUserId(@Param("userId") UUID userId);
}
