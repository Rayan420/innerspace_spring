package com.innerspaces.innerspace.config;

import com.innerspaces.innerspace.services.auth.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {




    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract token type from JWT claims
        String tokenType = jwt.getClaimAsString("token_type");

        // Assuming your token type for access tokens is "access"
        if ("access".equalsIgnoreCase(tokenType)) {
            // Extract user roles from JWT claims
            String role = jwt.getClaimAsString("role");

            // Add user roles to authorities
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            // Create JwtAuthenticationToken with authorities
            return new JwtAuthenticationToken(jwt, authorities);
        } else {
            // For other token types or unknown token types, return null (no authentication)
            return null;
        }
    }
}
