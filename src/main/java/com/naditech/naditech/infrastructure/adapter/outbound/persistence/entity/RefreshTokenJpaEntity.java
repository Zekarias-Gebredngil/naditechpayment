package com.naditech.naditech.infrastructure.adapter.outbound.persistence.entity;

import com.naditech.naditech.domain.model.ClientType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_token_value", columnList = "token", unique = true),
        @Index(name = "idx_token_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @Column(length = 255)
    private String replacedByToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ClientType clientType = ClientType.UNKNOWN;

    @Column(length = 100)
    private String deviceId;

    @Column(length = 100)
    private String deviceName;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}
