package com.innerspaces.innerspace.services.auth;

import com.innerspaces.innerspace.entities.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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


        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .expiresAt(exp)
                .id(UUID.randomUUID().toString())
                .claim("role", scope)
                .claims(stringObjectMap -> stringObjectMap.put("token_type", "access"))
                .build();
        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .expiresAt(refreshExp)
                .id(UUID.randomUUID().toString())
                .claim("role", scope)
                .claims(stringObjectMap -> stringObjectMap.put("token_type", "refresh"))
                .build();
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access", jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue());
        tokens.put("refresh", jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue());

        return tokens ;
    }

    public String generateOtpToken(String otp, ApplicationUser user)
    {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(150);

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
            System.out.println("Verifying OTP token...");

            Jwt jwt = jwtDecoder.decode(token);

            // Print decoded token information for debugging
            System.out.println("Decoded JWT: "+ jwtDecoder.decode(token).getTokenValue());
//            System.out.println("Subject: " + jwtDecoder.decode(token).getSubject());
            System.out.println("Expires At: " + jwtDecoder.decode(token).getExpiresAt());
            System.out.println("Claims: " + jwtDecoder.decode(token).getClaims());

            // Check if token has expired
            Instant now = Instant.now();
            Instant expiresAt = jwt.getExpiresAt();
            if (expiresAt != null && expiresAt.isBefore(now)) {
                System.out.println("Token has expired");
                return false; // Token expired
            }

            // Check if token issuer is valid (optional)
//            if (!"self".equals(jwt.getIssuer())) {
//                System.out.println("Invalid issuer: " + jwt.getIssuer());
//                return false; // Invalid issuer
//            }

            // Check if token has required claims
            Map<String, Object> claims = jwt.getClaims();
            if (!claims.containsKey("otp")) {
                System.out.println("Token does not contain OTP claim");
                return false; // Missing OTP claim
            }

            // Check if token type is "otp_validation"
            if (!"otp_validation".equals(claims.get("token_type"))) {
                System.out.println("Invalid token type: " + claims.get("token_type"));
                return false; // Invalid token type
            }

            // Validate that the email from token matches the user's email
            if (!user.getEmail().equals(jwt.getSubject())) {
                System.out.println("Email mismatch: " + user.getEmail() + " vs. " + jwt.getSubject());
                return false; // Email mismatch
            }

            // Token is valid
            System.out.println("OTP token is valid");
            return true;
        } catch (Exception e) {
            System.out.println("Token parsing or verification failed: " + e.getMessage());
            return false; // Token parsing or verification failed
        }
    }




}
