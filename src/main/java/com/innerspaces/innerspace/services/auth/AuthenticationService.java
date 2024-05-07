package com.innerspaces.innerspace.services.auth;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.innerspaces.innerspace.controller.auth.AuthenticationController;
import com.innerspaces.innerspace.entities.ForgotPassword;
import com.innerspaces.innerspace.exceptions.UsernameTaken;
import com.innerspaces.innerspace.models.EmailModel;
import com.innerspaces.innerspace.exceptions.RoleDoesNotExistException;
import com.innerspaces.innerspace.exceptions.EmailTaken;
import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.models.auth.*;
import com.innerspaces.innerspace.entities.Role;
import com.innerspaces.innerspace.entities.UserProfile;
import com.innerspaces.innerspace.repositories.auth.ForgotPasswordRepository;
import com.innerspaces.innerspace.repositories.user.RoleRepository;
import com.innerspaces.innerspace.repositories.user.UserProfileRepository;
import com.innerspaces.innerspace.repositories.user.UserRepository;
import com.innerspaces.innerspace.services.EmailServiceImpl;
import com.innerspaces.innerspace.services.user.UserService;
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
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final SecurityContextHolder holder;

    private final UserService userService;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, TokenService tokenService, RoleRepository roleRepo, PasswordEncoder passwordEncoder, UserRepository userRepo, UserProfileRepository profileRepo, EmailServiceImpl emailService, ForgotPasswordRepository fpRepo, Key secrectKey, TimeBasedOneTimePasswordGenerator totp, SecurityContextHolder holder, UserService userService)
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
        this.holder = holder;
        this.userService = userService;
    }


    public void registerUser(RegistrationObject ro) throws UsernameTaken, EmailTaken{
       try
       {


           System.out.println("EMail present " + userService.emailExist(ro.getEmail()) +" " + "usernamme present "+ userService.usernameExist(ro.getUsername()));
           if(userService.emailExist(ro.getEmail().toLowerCase()))
           {
               throw new EmailTaken(ro.getEmail().toLowerCase());
           }
           else if(userService.usernameExist(ro.getUsername().toLowerCase()))
           {
               logger.warn("username duplicate {}" + ro.getUsername());
               List<String> names;
               UsernameRecomAlgo recommendation = new UsernameRecomAlgo(userRepo);
               names = recommendation.usernameRecommendation(ro.getUsername());

               throw new UsernameTaken("username taken try these instead : ", names);

           }
           else
           {
               ApplicationUser user = new ApplicationUser();
               HashSet<Role> roles = new HashSet<>();

               roles.add(roleRepo.findByAuthority("USER").orElseThrow(RoleDoesNotExistException::new));
               user.setAuthorities(roles);
               logger.info("Received registration request for user password: {}", ro.getPassword());

               // Set the user for the profile
               logger.info("Received registration request for user: {}", ro.getUsername() + ro.getEmail());
               UserProfile profile = new UserProfile();
               profile.setUser(user);
               user.setFirstName(ro.getFirstName());
               user.setLastName(ro.getLastName());
               user.setEmail(ro.getEmail().toLowerCase());
               user.setUserProfile(profile);


               user.setUsername(ro.getUsername().toLowerCase());
               String encryptPass = passwordEncoder.encode(ro.getPassword());
               user.setPassword(encryptPass);
               logger.info("Received registration request for user encrypted password: {}",encryptPass);
               userRepo.save(user);
               EmailModel model = new EmailModel(user.getEmail(), "Welcome to Innerspace", "welcome_email", user.getFirstName(), user.getLastName());
               Context context = new Context();
               context.setVariable("firstName", ro.getFirstName());
               context.setVariable("lastName", ro.getLastName());


               emailService.sendRegistrationEmail(model, context );
           }

       }
       catch (UsernameTaken | EmailTaken e)
       {
           throw e;
       }


    }

    public ApplicationUser setUserProfile(ProfileDTO dto, String username) throws UsernameNotFoundException {
        ApplicationUser user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("invalid user"));
        UserProfile profile = profileRepo.findUserProfileByUser(user).orElse(new UserProfile());
        profile.setBio(dto.getBio());

        logger.info("Received bio: {}", dto.getBio());

        if(dto.getProfilePicture() != null) {
            logger.info("Received profile picture of size: {}", dto.getProfilePicture().length);
        } else {
            logger.warn("No profile picture received.");
        }

        profile.setProfilePicture(dto.getProfilePicture());
        profileRepo.save(profile);
        user.setUserProfile(profile);
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


    public ResponseEntity<MessageDTO> logout(String username) {

        ApplicationUser user = userService.getUserByUsername(username);
        user.setRefreshId(null);
        MessageDTO dto = new MessageDTO("Logout successful");
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public String generateOTP() throws InvalidKeyException {
        Instant now = Instant.now();
        return totp.generateOneTimePasswordString(secrectKey, now );
    }

    public ForgotPasswordResponseDTO sendForgotPasswordEmail(String email) throws InvalidKeyException, MessagingException {
        Optional<ApplicationUser> optionalUser = userRepo.findByEmail(email);
        if(optionalUser.isPresent()) {
            ApplicationUser user = optionalUser.get();
            EmailModel model = new EmailModel(user.getEmail(),"Welcome to innerspace", "password_reset");
            Context context = new Context();
            String otp = generateOTP(); // Generate a new OTP each time
            System.out.println("Your OTP is " + otp);
            context.setVariable("otp", otp);

            // Check if there's an existing record for the user
            Optional<ForgotPassword> existingRecord = fpRepo.findByUser(user);
            if(existingRecord.isPresent()) {
                // Update the existing OTP record with the new OTP
                ForgotPassword existingForgotPassword = existingRecord.get();
                existingForgotPassword.setOtp(otp);
                existingForgotPassword.setExpired(false);
                existingForgotPassword.setExpirationDate(Instant.now().plusSeconds(120));

                fpRepo.save(existingForgotPassword);
            } else {
                // Create a new OTP record for the user with the new OTP
                ForgotPassword newForgotPassword = new ForgotPassword();
                newForgotPassword.setOtp(otp);
                newForgotPassword.setUser(user);
                newForgotPassword.setExpirationDate(Instant.now().plusSeconds(120));
                fpRepo.save(newForgotPassword);
            }

            // Send the email with the new OTP
            emailService.sendForgotPasswordEmail(model, otp, context);

            return new ForgotPasswordResponseDTO("Reset link has been sent to your email");
        }
        return new ForgotPasswordResponseDTO("Reset link has been sent to your email");
    }


    public ResponseEntity<ForgotPasswordResponseDTO> verifyOTP(String email, String otp) {
        logger.info("information recieved " + email + otp);

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


    public ResponseEntity<MessageDTO> changePassword(String email, ForgotPasswordDTO dto)
    throws UsernameNotFoundException{
       ApplicationUser user = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("invalid user"));
        if(tokenService.verifyOtpToken(dto.getToken(), user))
        {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            userRepo.save(user);
            return new ResponseEntity<>(new MessageDTO("Password changed successfully, you can now login"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageDTO("Password reset failed, something went wrong"), HttpStatus.BAD_REQUEST);


    }


    public void test()
    {

        System.out.println(holder.toString());
    }


}
