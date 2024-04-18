package com.innerspaces.innerspace.services.auth;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.innerspaces.innerspace.controller.auth.AuthenticationController;
import com.innerspaces.innerspace.entities.ForgotPassword;
import com.innerspaces.innerspace.models.EmailModel;
import com.innerspaces.innerspace.exceptions.RoleDoesNotExistException;
import com.innerspaces.innerspace.exceptions.UsernameOrEmailAlreadyTaken;
import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.models.auth.LoginResponseDTO;
import com.innerspaces.innerspace.models.auth.RegistrationObject;
import com.innerspaces.innerspace.entities.Role;
import com.innerspaces.innerspace.entities.UserProfile;
import com.innerspaces.innerspace.repositories.auth.ForgotPasswordRepository;
import com.innerspaces.innerspace.repositories.user.RoleRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import com.innerspaces.innerspace.services.EmailServiceImpl;
import com.innerspaces.innerspace.utils.UsernameRecomAlgo;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.security.InvalidKeyException;
import java.security.Key;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;
    private final EmailServiceImpl emailService;
    private final ForgotPasswordRepository fpRepo;

    private final Key secrectKey;
    private final TimeBasedOneTimePasswordGenerator totp;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, TokenService tokenService, RoleRepository roleRepo, PasswordEncoder passwordEncoder, UserRepository userRepo, EmailServiceImpl emailService, ForgotPasswordRepository fpRepo, Key secrectKey, TimeBasedOneTimePasswordGenerator totp)
    {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.fpRepo = fpRepo;
        this.secrectKey = secrectKey;
        this.totp = totp;
    }


    public Boolean registerUser(RegistrationObject ro) throws Exception {
            ApplicationUser user = new ApplicationUser();
            HashSet<Role> roles = new HashSet<>();

            roles.add(roleRepo.findByAuthority("USER").orElseThrow(RoleDoesNotExistException::new));
            user.setAuthorities(roles);
            logger.info("Received registration request for user password: {}", ro.getPassword());

            // Set the user for the profile
            logger.info("Received registration request for user: {}", ro);
            UserProfile profile = new UserProfile();
            profile.setBio(ro.getBio());
            profile.setProfilePictureUrl(ro.getProfilePictureUrl());
            profile.setUser(user);
            logger.info("user profile created: {}", user.getUserProfile()); // This will log the profile data

            if(userRepo.findByEmail(ro.getEmail()).isPresent())
            {
                throw new UsernameOrEmailAlreadyTaken(ro.getEmail().toLowerCase());
            }
            user.setEmail(ro.getEmail().toLowerCase());
            user.setFirstName(ro.getFirstName().toLowerCase());
            user.setLastName(ro.getLastName().toLowerCase());
            user.setDateOfBirth(ro.getDob());
            user.setUserProfile(profile);

            if(userRepo.findByUsername(ro.getUsername()).isPresent())
            {
                List<String> names = new ArrayList<>();
                UsernameRecomAlgo recommendation = new UsernameRecomAlgo(userRepo);
                names = recommendation.usernameRecommendation(ro.getUsername());
                throw new UsernameOrEmailAlreadyTaken(names, ro.getUsername().toLowerCase());

            }
            user.setUsername(ro.getUsername().toLowerCase());
            String encryptPass = passwordEncoder.encode(ro.getPassword());
            user.setPassword(encryptPass);
            logger.info("Received registration request for user encrypted password: {}",encryptPass);
            userRepo.save(user);
            EmailModel model = new EmailModel( user.getFirstName(), user.getLastName(), user.getEmail(),"Welcome to innerspace", "welcome_email");
            Context context = new Context();
            context.setVariable("firstname", ro.getFirstName());
            context.setVariable("lastname", ro.getLastName());

            emailService.sendRegistrationEmail(model, context );

            return true;


    }


    public LoginResponseDTO loginUser(String username, String password){
        logger.info("Received credentials: Username: {}, Password: {}", username, password);

        try{
            // Authenticate the user
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username.toLowerCase(), password)
            );

            // Check if authentication was successful
            if (auth.isAuthenticated()) {
                logger.info("User {} authenticated successfully", username);

                // Generate JWT token
                Map<String, String> tokens = tokenService.generateJwt(auth);

                // Retrieve user from repository
                Optional<ApplicationUser> optionalUser = userRepo.findByUsername(username.toLowerCase());
                if (optionalUser.isPresent()) {
                    ApplicationUser user = optionalUser.get();

                    // Log the user details
                    logger.info("User details retrieved: {}", user);
                    user.setLastLogin();

                    // Return LoginResponseDTO with user and token
                    return new LoginResponseDTO(user, tokens);
                } else {
                    logger.error("User {} not found in the repository", username);
                    // Handle case where user is not found
                    return new LoginResponseDTO(null, null);
                }
            } else {
                // Handle case where authentication failed
                logger.error("Authentication failed for user {}", username);
                return new LoginResponseDTO(null, null);
            }
        } catch(AuthenticationException e){
            // Handle authentication exception
            logger.error("Authentication exception for user {}: {}", username, e.getMessage());
            return new LoginResponseDTO(null, null);
        }
    }


    public String generateOTP() throws InvalidKeyException {
        try{
            Instant now = Instant.now();
            return totp.generateOneTimePasswordString(secrectKey, now );
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResponseEntity<?>sendForgotPasswordEmail(String email) throws InvalidKeyException, MessagingException {
        if(userRepo.findByEmail(email).isPresent())
        {
            ApplicationUser user = userRepo.findByEmail(email).get();
            EmailModel model = new EmailModel( user.getEmail(),"Welcome to innerspace", "password_reset");
            Context context = new Context();
            String otp = generateOTP();
            System.out.println("your otp is "+ otp);
            context.setVariable("otp", otp );
            ForgotPassword fpotp = new ForgotPassword();
            fpotp.setOtp(otp);
            fpotp.setUser(user);
            fpotp.setExpirationDate(Instant.now().plusSeconds(300));
            fpRepo.save(fpotp);
            emailService.sendForgotPasswordEmail(model, otp, context);
            return new ResponseEntity<>("Reset link has been sent to your email", HttpStatus.OK);
        }
        return new ResponseEntity<>("Reset link has been sent to your email", HttpStatus.OK);

    }

    public ResponseEntity<String> verifyOTP(String email, String otp) {
        ApplicationUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Optional<ForgotPassword> forgotPasswordOpt = fpRepo.findByOtpAndUser(otp, user);
        if (forgotPasswordOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP code");
        }

        ForgotPassword fp = forgotPasswordOpt.get();
        if (!fp.getOtp().equals(otp) || !fp.getUser().equals(user) || fp.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP code is Invalid or has Expired");
        }
        if(Instant.now().isAfter(fp.getExpirationDate()))
        {
            fp.setExpired(true);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP code has expired");
        }

        fp.setExpired(true);
        // OTP is valid
        return ResponseEntity.ok("OTP Validated");
    }




}
