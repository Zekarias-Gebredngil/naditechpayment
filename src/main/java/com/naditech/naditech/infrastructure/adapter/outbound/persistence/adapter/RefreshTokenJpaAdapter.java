// Outbound persistence adapter implementing domain RefreshTokenRepositoryPort
package com.naditech.naditech.infrastructure.adapter.outbound.persistence.adapter;

import com.naditech.naditech.domain.model.RefreshToken;
import com.naditech.naditech.domain.port.RefreshTokenRepositoryPort;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.entity.RefreshTokenJpaEntity;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.mapper.RefreshTokenEntityMapper;
import com.naditech.naditech.infrastructure.adapter.outbound.persistence.repository.SpringDataRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenJpaAdapter implements RefreshTokenRepositoryPort {

    private final SpringDataRefreshTokenRepository springDataRefreshTokenRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return springDataRefreshTokenRepository.findByTokenWithUser(token)
                .map(RefreshTokenEntityMapper::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity jpaEntity = RefreshTokenEntityMapper.toJpaEntity(refreshToken);
        RefreshTokenJpaEntity saved = springDataRefreshTokenRepository.save(jpaEntity);
        return RefreshTokenEntityMapper.toDomain(saved);
    }

    @Override
    public void revokeAllUserTokens(UUID userId) {
        springDataRefreshTokenRepository.revokeAllByUserId(userId);
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        springDataRefreshTokenRepository.deleteById(refreshToken.getId());
    }
}
