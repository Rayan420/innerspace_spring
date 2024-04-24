package com.innerspaces.innerspace.controller.auth;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.exceptions.RoleDoesNotExistException;
import com.innerspaces.innerspace.exceptions.UsernameOrEmailAlreadyTaken;
import com.innerspaces.innerspace.models.auth.ForgotPasswordDTO;
import com.innerspaces.innerspace.models.auth.ForgotPasswordResponseDTO;
import com.innerspaces.innerspace.models.auth.LoginObject;
import com.innerspaces.innerspace.models.auth.LoginResponseDTO;
import com.innerspaces.innerspace.models.auth.ProfileDTO;
import com.innerspaces.innerspace.models.auth.RegistrationObject;
import com.innerspaces.innerspace.services.auth.AuthenticationService;
import com.innerspaces.innerspace.services.user.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidKeyException;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationService authService;

    @Autowired
    public AuthenticationController(UserService userService, AuthenticationService authService) {
        this.authService = authService;
    }

    @ExceptionHandler({UsernameOrEmailAlreadyTaken.class})
    public ResponseEntity<String> handleEmailTaken(UsernameOrEmailAlreadyTaken ex)
    {
        String message = ex.getMessage();
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(RoleDoesNotExistException.class)
    public ResponseEntity<String> handleRoleDoesNotExistException()
    {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = {"/register", "/register/"}, method = RequestMethod.POST, params = {})
    public ResponseEntity<?> registerUser(@RequestBody RegistrationObject ro) {
        try {
            // Attempt to register the user
            authService.registerUser(ro);
        } catch (UsernameOrEmailAlreadyTaken e) {
            // Handle the custom exception for duplicate username or email
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (RoleDoesNotExistException e) {
            // Handle the custom exception for non-existing role
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle any other exceptions during registration
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return loginUserAfterRegistration(ro);
    }

    private ResponseEntity<?> loginUserAfterRegistration(RegistrationObject ro) {
        try {
            // Attempt to log the user
            LoginResponseDTO loginResponse = authService.loginUser(ro.getUsername(), ro.getPassword());
            if (loginResponse != null && loginResponse.getTokens() != null) {
                // If login is successful, return the login response
                return new ResponseEntity<>(loginResponse, HttpStatus.OK);
            } else {
                // If login fails, return an error response
                return new ResponseEntity<>("Login failed after registration", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            // Handle any exceptions during login after registration
            return new ResponseEntity<>("Login failed after registration: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = {"/register/{username}/", "/register/{username}"}, method = RequestMethod.POST)
    public ApplicationUser CompleteUserprofileSetup(@RequestBody ProfileDTO dto, @PathVariable String username)
    {
        System.out.println(dto);
        return authService.setUserProfile(dto, username);
    }


    @RequestMapping(value = {"/login", "/login/"}, method = RequestMethod.POST, params = {})
    public LoginResponseDTO loginUser(@RequestBody LoginObject lo) {

        System.out.println("login object from map:");
        System.out.println(lo.getUsername());
        System.out.println(lo.getPassword());

        return authService.loginUser(lo.getUsername(), lo.getPassword());
    }


    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<?> InvalidKeyException()
    {
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<?> MessagingException()
    {
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }
    @RequestMapping(value = {"/forgot-password/{email}/send", "/forgot-password/{email}/send/"},
    method = RequestMethod.POST, params = {})
    public ForgotPasswordResponseDTO forgotPassword(@PathVariable String email )
    {
        try {
            return authService.sendForgotPasswordEmail(email);
        } catch (InvalidKeyException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/forgot-password/{email}/otp", method = RequestMethod.POST)
    public ResponseEntity<ForgotPasswordResponseDTO> validateOTP(@PathVariable String email, @RequestParam("verify") String otp) {
        System.out.println(otp);
        return authService.verifyOTP(email, otp);
    }

    @RequestMapping(value = {"/forgot-password/{email}/change"}, method = RequestMethod.POST)
    public ResponseEntity<?> changePassword(@PathVariable String email, @RequestBody ForgotPasswordDTO body)
    {
        return authService.changePassword(email, body);
    }




}