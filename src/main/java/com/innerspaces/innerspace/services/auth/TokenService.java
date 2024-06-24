package com.innerspaces.innerspace.services.auth;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.exceptions.TokenExpiredException;
import com.innerspaces.innerspace.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserService userService;

    @Autowired
    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserService userService) {
        this.jwtDecoder = jwtDecoder;
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
    }

    public Map<String, String> generateJwt(Authentication auth) {
        Instant now = Instant.now();
        Instant accessTokenExp = now.plusSeconds(1800);
        Instant refreshTokenExp = now.plusSeconds(604800);

        String refreshId = UUID.randomUUID().toString();
        ApplicationUser user = userService.getUserByUsername(auth.getName());
        user.setRefreshId(refreshId);
        userService.saveUser(user);

        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .expiresAt(accessTokenExp)
                .claim("role", "USER")
                .claim("refreshId", refreshId)
                .claims(stringObjectMap -> stringObjectMap.put("token_type", "access"))
                .build();

        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .expiresAt(refreshTokenExp)
                .claim("role", "USER")
                .claim("refreshId", refreshId)
                .claims(stringObjectMap -> stringObjectMap.put("token_type", "refresh"))
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access", accessToken);
        tokens.put("refresh", refreshToken);
        return tokens;
    }

    public String validateAndRefreshToken(String refreshToken) {
        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);
            Instant now = Instant.now();
            Instant expiresAt = jwt.getExpiresAt();
            if (expiresAt != null && expiresAt.isBefore(now)) {
                throw new TokenExpiredException("Refresh token has expired");
            }

            String refreshId = (String) jwt.getClaims().get("refreshId");
            ApplicationUser user = userService.getUserByRefreshId(refreshId);
            if (user == null) {
                throw new UsernameNotFoundException("user not found");
            }

            return generateAccessToken(user);
        } catch (JwtException | UsernameNotFoundException | TokenExpiredException e) {
            return null;
        }
    }

    private String generateAccessToken(ApplicationUser user) {
        Instant now = Instant.now();
        Instant accessTokenExp = now.plusSeconds(1800);
        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(user.getUsername())
                .expiresAt(accessTokenExp)
                .claim("role", "USER")
                .claims(stringObjectMap -> stringObjectMap.put("token_type", "access"))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();
    }

    public String generateOtpToken(String otp, ApplicationUser user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(12000);

        JwtClaimsSet otpToken = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(user.getEmail())
                .expiresAt(exp)
                .id(UUID.randomUUID().toString())
                .claim("otp", otp)
                .claims(stringObjectMap -> stringObjectMap.put("token_type", "otp_validation"))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(otpToken)).getTokenValue();
    }

    public boolean verifyOtpToken(String token, ApplicationUser user) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Instant now = Instant.now();
            Instant expiresAt = jwt.getExpiresAt();
            if (expiresAt != null && expiresAt.isBefore(now)) {
                return false;
            }

            Map<String, Object> claims = jwt.getClaims();
            if (!claims.containsKey("otp") || !"otp_validation".equals(claims.get("token_type"))) {
                return false;
            }

            return user.getEmail().equals(jwt.getSubject());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean accessTokenValid(String refreshId) {
        ApplicationUser user = userService.getUserByRefreshId(refreshId);
        return user != null;
    }

    public String getUsernameFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return Objects.requireNonNull(jwt.getExpiresAt()).isAfter(Instant.now());
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            return jwtDecoder.decode(token).getSubject().equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (TokenExpiredException ex) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return Objects.requireNonNull(jwtDecoder.decode(token).getExpiresAt()).isBefore(new Date(System.currentTimeMillis()).toInstant());
        } catch (TokenExpiredException ex) {
            return true;
        }
    }

    public String getRefreshIdFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return (String) jwt.getClaims().get("refreshId");
        } catch (JwtException e) {
            return null;
        }
    }
}
