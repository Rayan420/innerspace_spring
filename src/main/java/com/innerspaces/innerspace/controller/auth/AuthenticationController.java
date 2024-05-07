package com.innerspaces.innerspace.controller.auth;

import com.innerspaces.innerspace.entities.ApplicationUser;
import com.innerspaces.innerspace.entities.ProfileImage;
import com.innerspaces.innerspace.exceptions.RoleDoesNotExistException;
import com.innerspaces.innerspace.exceptions.EmailTaken;
import com.innerspaces.innerspace.exceptions.UsernameTaken;
import com.innerspaces.innerspace.models.auth.*;
import com.innerspaces.innerspace.services.auth.AuthenticationService;
import com.innerspaces.innerspace.services.user.UserService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;


@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authService;


    @Autowired
    public AuthenticationController(UserService userService, AuthenticationService authService) {
        this.authService = authService;
    }

    @ExceptionHandler({EmailTaken.class})
    public ResponseEntity<String> handleEmailTaken(EmailTaken ex)
    {
        String message = ex.getMessage();
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(RoleDoesNotExistException.class)
    public ResponseEntity<String> handleRoleDoesNotExistException()
    {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Exception handler for UsernameTaken exception
    @ExceptionHandler(UsernameTaken.class)
    public ResponseEntity<String> handleUsernameTaken(UsernameTaken ex) {
        String message = ex.getMessage();
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @RequestMapping(value = {"/register", "/register/"}, method = RequestMethod.POST, params = {})
    public ResponseEntity<?> registerUser(@RequestBody RegistrationObject ro) {
        try {
            // Attempt to register the user
            authService.registerUser(ro);
            // If registration succeeds, return login response
            return loginUserAfterRegistration(ro);
        } catch (EmailTaken e) {
            // Handle the custom exception for duplicate username or email
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (RoleDoesNotExistException e) {
            // Handle the custom exception for non-existing role
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (UsernameTaken e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
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
        System.out.println("info sent by user to verify otp " + otp + email);
        return authService.verifyOTP(email, otp);
    }

    @RequestMapping(value = {"/forgot-password/{email}/change"}, method = RequestMethod.POST)
    public ResponseEntity<MessageDTO> changePassword(@PathVariable String email, @RequestBody ForgotPasswordDTO body)
    {
        return authService.changePassword(email, body);
    }


    @RequestMapping(value = { "/logout/{username}"},
            method = RequestMethod.POST)
    public ResponseEntity<MessageDTO> logout(@PathVariable String username)
    {
        return authService.logout(username);
    }


    @RequestMapping(value = { "/refresh/"},
            method = RequestMethod.POST )
    public ResponseEntity<?> refreshToken(@RequestParam("Token") String token)
    {
        System.out.println("--------TOKEN ---------- "+token);
        String newToken = authService.refreshToken(token);
        // return the new token
      if(newToken != null)
      {
          return new ResponseEntity<>(new Refresh(newToken), HttpStatus.OK);
      }
      else
      {
          // return an error message saying token is invalid
            return new ResponseEntity<>(new MessageDTO("Token is expired or does not exist"), HttpStatus.CONFLICT);
      }

    }


}
