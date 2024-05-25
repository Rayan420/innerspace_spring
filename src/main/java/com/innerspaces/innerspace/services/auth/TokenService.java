package com.innerspaces.innerspace.services.auth;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.exceptions.TokenExpiredException;
import com.innerspaces.innerspace.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

   private final UserService userService;



    @Autowired
    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserService userService)
    {
        this.jwtDecoder = jwtDecoder;
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
    }

    public Map<String, String> generateJwt(Authentication auth) {
        // Get current time
        Instant now = Instant.now();

        // Set expiration time for access token (30 minutes)
        Instant accessTokenExp = now.plusSeconds(1800);

        // Set expiration time for refresh token (1 week)
        Instant refreshTokenExp = now.plusSeconds(604800);

        // Generate refreshId (UUID) for the user
        String refreshId = UUID.randomUUID().toString();

        // Retrieve user information
        ApplicationUser user = userService.getUserByUsername(auth.getName());
        user.setRefreshId(refreshId);
        userService.saveUser(user);

        // Generate access token claims
        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .expiresAt(accessTokenExp)
                .claim("role", "USER") // Example role, replace with actual user roles
                .claim("refreshId", refreshId)
                .claims(stringObjectMap -> stringObjectMap.put("token_type", "access"))
                .build();

        // Generate refresh token claims
        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .expiresAt(refreshTokenExp)
                .claim("role", "USER") // Example role, replace with actual user roles
                .claim("refreshId", refreshId)
                .claims(stringObjectMap -> stringObjectMap.put("token_type", "refresh"))
                .build();

        // Encode tokens
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();

        // Return tokens
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access", accessToken);
        tokens.put("refresh", refreshToken);
        return tokens;
    }

    public String validateAndRefreshToken(String refreshToken) {
        try {
            // Decode refresh token
            Jwt jwt = jwtDecoder.decode(refreshToken);

            // Check if refresh token has expired
            Instant now = Instant.now();
            Instant expiresAt = jwt.getExpiresAt();
            if (expiresAt != null && expiresAt.isBefore(now)) {
                throw new TokenExpiredException("Refresh token has expired");
            }

            // Retrieve refreshId from token claims
            String refreshId = (String) jwt.getClaims().get("refreshId");

            // Retrieve user by refreshId from the database
            ApplicationUser user = userService.getUserByRefreshId(refreshId);
            if (user == null) {
                throw new UsernameNotFoundException("user not found");
            }

            // Generate new access token
            return generateAccessToken(user);
        } catch (JwtException | UsernameNotFoundException | TokenExpiredException e) {
            // Handle token validation errors
            // Log error or throw custom exceptions
            return null;
        }
    }
    private String generateAccessToken(ApplicationUser user) {
        // Generate new access token claims
        Instant now = Instant.now();
        Instant accessTokenExp = now.plusSeconds(1800); // 30 minutes expiration for access token
        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(user.getUsername())
                .expiresAt(accessTokenExp)
                .claim("role", "USER") // Example role, replace with actual user roles
                .claims(stringObjectMap -> stringObjectMap.put("token_type", "access"))
                .build();

        // Encode access token
        return jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();
    }
    public String generateOtpToken(String otp, ApplicationUser user)
    {
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


    public boolean accessTokenValid(String refreshId)
    {
        ApplicationUser user = userService.getUserByRefreshId(refreshId);
        return user != null;
    }




}
