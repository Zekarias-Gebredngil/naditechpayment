package com.naditech.naditech.infrastructure.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {

    public static final String GATEWAY_HEADER_USER_ID = "X-User-Id";
    public static final String GATEWAY_HEADER_USER_EMAIL = "X-User-Email";
    public static final String GATEWAY_HEADER_USER_ROLE = "X-User-Role";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String gatewayEmail = request.getHeader(GATEWAY_HEADER_USER_EMAIL);
        String gatewayRole = request.getHeader(GATEWAY_HEADER_USER_ROLE);

        if (StringUtils.hasText(gatewayEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String roleName = StringUtils.hasText(gatewayRole) ? gatewayRole.trim() : "ROLE_USER";
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }

            var authorities = List.of(new SimpleGrantedAuthority(roleName));
            var authToken = new UsernamePasswordAuthenticationToken(gatewayEmail, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.debug("Authenticated via API Gateway headers for user: {} with role: {}", gatewayEmail, roleName);
        }

        filterChain.doFilter(request, response);
    }
}
