package com.innerspaces.innerspace.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Autowired
    public TokenService(JwtEncoder jwtEncoder,JwtDecoder jwtDecoder)
    {
        this.jwtDecoder = jwtDecoder;
        this.jwtEncoder = jwtEncoder;
    }

    public Map<String, String> generateJwt(Authentication auth)
    {

        Instant now = Instant.now();
        Instant exp = now.plusMillis(86_400_000);
        Instant refreshExp = now.plusSeconds(2_629_746);

        String scope = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));


        JwtClaimsSet accescClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .expiresAt(exp)
                .id(UUID.randomUUID().toString())
                .claim("role", scope)
                .claims(new Consumer<Map<String, Object>>() {
                    @Override
                    public void accept(Map<String, Object> stringObjectMap) {
                        stringObjectMap.put("token_type", "access");
                    }
                })
                .build();
        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .expiresAt(refreshExp)
                .id(UUID.randomUUID().toString())
                .claim("role", scope)
                .claims(new Consumer<Map<String, Object>>() {
                    @Override
                    public void accept(Map<String, Object> stringObjectMap) {
                        stringObjectMap.put("token_type", "refresh");
                    }
                })
                .build();
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access", jwtEncoder.encode(JwtEncoderParameters.from(accescClaims)).getTokenValue());
        tokens.put("refresh", jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue());

        return tokens ;
    }

}
