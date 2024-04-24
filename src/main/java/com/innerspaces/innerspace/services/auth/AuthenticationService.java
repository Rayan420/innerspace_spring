package com.innerspaces.innerspace.services.auth;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.innerspaces.innerspace.controller.auth.AuthenticationController;
import com.innerspaces.innerspace.entities.ForgotPassword;
import com.innerspaces.innerspace.models.EmailModel;
import com.innerspaces.innerspace.exceptions.RoleDoesNotExistException;
import com.innerspaces.innerspace.exceptions.UsernameOrEmailAlreadyTaken;
import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.models.auth.ForgotPasswordDTO;
import com.innerspaces.innerspace.models.auth.ForgotPasswordResponseDTO;
import com.innerspaces.innerspace.models.auth.LoginResponseDTO;
import com.innerspaces.innerspace.models.auth.ProfileDTO;
import com.innerspaces.innerspace.models.auth.RegistrationObject;
import com.innerspaces.innerspace.entities.Role;
import com.innerspaces.innerspace.entities.UserProfile;
import com.innerspaces.innerspace.repositories.auth.ForgotPasswordRepository;
import com.innerspaces.innerspace.repositories.user.RoleRepository;
import com.innerspaces.innerspace.repositories.user.UserProfileRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final UserProfileRepository profileRepo;
    private final EmailServiceImpl emailService;
    private final ForgotPasswordRepository fpRepo;

    private final Key secrectKey;
    private final TimeBasedOneTimePasswordGenerator totp;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, TokenService tokenService, RoleRepository roleRepo, PasswordEncoder passwordEncoder, UserRepository userRepo, UserProfileRepository profileRepo, EmailServiceImpl emailService, ForgotPasswordRepository fpRepo, Key secrectKey, TimeBasedOneTimePasswordGenerator totp)
    {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.emailService = emailService;
        this.fpRepo = fpRepo;
        this.secrectKey = secrectKey;
        this.totp = totp;
    }


    public void registerUser(RegistrationObject ro) {
            ApplicationUser user = new ApplicationUser();
            HashSet<Role> roles = new HashSet<>();

            roles.add(roleRepo.findByAuthority("USER").orElseThrow(RoleDoesNotExistException::new));
            user.setAuthorities(roles);
            logger.info("Received registration request for user password: {}", ro.getPassword());

            // Set the user for the profile
            logger.info("Received registration request for user: {}", ro);
            UserProfile profile = new UserProfile();
            profile.setUser(user);
            logger.info("user profile created: {}", user.getUserProfile()); // This will log the profile data

            if(userRepo.findByEmail(ro.getEmail()).isPresent())
            {
                throw new UsernameOrEmailAlreadyTaken(ro.getEmail().toLowerCase());
            }
            user.setEmail(ro.getEmail().toLowerCase());
            user.setUserProfile(profile);

            if(userRepo.findByUsername(ro.getUsername()).isPresent())
            {
                List<String> names;
                UsernameRecomAlgo recommendation = new UsernameRecomAlgo(userRepo);
                names = recommendation.usernameRecommendation(ro.getUsername());
                throw new UsernameOrEmailAlreadyTaken(names, ro.getUsername().toLowerCase());

            }
            user.setUsername(ro.getUsername().toLowerCase());
            String encryptPass = passwordEncoder.encode(ro.getPassword());
            user.setPassword(encryptPass);
            logger.info("Received registration request for user encrypted password: {}",encryptPass);
            userRepo.save(user);
            EmailModel model = new EmailModel( user.getUsername(), user.getEmail(),"Welcome to innerspace", "welcome_email");
            Context context = new Context();
            context.setVariable("username", ro.getUsername());

            emailService.sendRegistrationEmail(model, context );


    }

    public ApplicationUser setUserProfile(ProfileDTO dto, String username)
    throws UsernameNotFoundException{
        ApplicationUser user =  userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("invalid user"));
        UserProfile profile = profileRepo.findUserProfileByUser(user).orElse(new UserProfile());
        profile.setBio(dto.getBio());
        profile.setProfilePictureUrl(dto.getProfilePictureUrl());
        profileRepo.save(profile);
        user.setUserProfile(profile);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDateOfBirth(dto.getDob());
        userRepo.save(user);

        return user;
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
        Instant now = Instant.now();
        return totp.generateOneTimePasswordString(secrectKey, now );
    }

    public ForgotPasswordResponseDTO sendForgotPasswordEmail(String email) throws InvalidKeyException, MessagingException {
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
            return new ForgotPasswordResponseDTO("Reset link has been sent to your email");
        }
        return new ForgotPasswordResponseDTO("Reset link has been sent to your email");

    }

    public ResponseEntity<ForgotPasswordResponseDTO> verifyOTP(String email, String otp) {
        ApplicationUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Optional<ForgotPassword> forgotPasswordOpt = fpRepo.findByOtpAndUser(otp, user);
        if (forgotPasswordOpt.isEmpty()) {
            ForgotPasswordResponseDTO dto = new ForgotPasswordResponseDTO();
            dto.setMessage("Invalid OTP code");
            return new ResponseEntity<>( dto, HttpStatus.CONFLICT);
        }

        ForgotPassword fp = forgotPasswordOpt.get();
        if (!fp.getOtp().equals(otp) || !fp.getUser().equals(user) || fp.isExpired()) {
            ForgotPasswordResponseDTO dto = new ForgotPasswordResponseDTO();
            dto.setMessage("OTP code is Invalid or has Expired");
            return new ResponseEntity<>( dto, HttpStatus.CONFLICT);
        }
        if(Instant.now().isAfter(fp.getExpirationDate()))
        {

            fp.setExpired(true);
            ForgotPasswordResponseDTO dto = new ForgotPasswordResponseDTO();
            dto.setMessage("OTP code has expired");
            return new ResponseEntity<>( dto, HttpStatus.CONFLICT);
        }

        ForgotPasswordResponseDTO dto = new ForgotPasswordResponseDTO(tokenService.generateOtpToken(otp, user),"OTP valid");
        fp.setExpired(true);
        // OTP is valid
        return new ResponseEntity<>(dto, HttpStatus.OK);

    }


    public ResponseEntity<?> changePassword(String email, ForgotPasswordDTO dto)
    throws UsernameNotFoundException{
       ApplicationUser user = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("invalid user"));
        if(tokenService.verifyOtpToken(dto.getToken(), user))
        {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            userRepo.save(user);
            return new ResponseEntity<>("Password changed successfully, you can now login", HttpStatus.OK);
        }
        return new ResponseEntity<>("Password reset failed, something went wrong", HttpStatus.BAD_REQUEST);


    }


}