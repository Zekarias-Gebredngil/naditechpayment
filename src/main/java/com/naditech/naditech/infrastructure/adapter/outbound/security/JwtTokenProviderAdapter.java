// Security adapter implementing TokenProviderPort using JJWT library
package com.naditech.naditech.infrastructure.adapter.outbound.security;

import com.naditech.naditech.application.port.out.TokenProviderPort;
import com.naditech.naditech.domain.model.ClientType;
import com.naditech.naditech.domain.model.Role;
import com.naditech.naditech.domain.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenProviderAdapter implements TokenProviderPort {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Override
    public String generateAccessToken(User user, ClientType clientType) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId().toString());
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("accountNumber", user.getAccountNumber());
        extraClaims.put("clientType", clientType != null ? clientType.name() : ClientType.UNKNOWN.name());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public UUID extractUserId(String token) {
        String userIdStr = (String) extractAllClaims(token).get("userId");
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }

    @Override
    public Role extractRole(String token) {
        String roleStr = (String) extractAllClaims(token).get("role");
        return roleStr != null ? Role.valueOf(roleStr) : Role.ROLE_USER;
    }

    @Override
    public long getExpiration() {
        return jwtExpiration;
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
