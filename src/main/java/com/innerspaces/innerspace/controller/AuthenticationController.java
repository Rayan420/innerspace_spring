package com.innerspaces.innerspace.controller;

import com.innerspaces.innerspace.exceptions.RoleDoesNotExistException;
import com.innerspaces.innerspace.exceptions.UsernameOrEmailAlreadyTaken;
import com.innerspaces.innerspace.models.auth.LoginObject;
import com.innerspaces.innerspace.models.auth.LoginResponseDTO;
import com.innerspaces.innerspace.models.auth.RegistrationObject;
import com.innerspaces.innerspace.services.AuthenticationService;
import com.innerspaces.innerspace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authService;

    @Autowired
    public AuthenticationController(UserService userService, AuthenticationService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @ExceptionHandler({UsernameOrEmailAlreadyTaken.class})
    public ResponseEntity<String> handleEmailTaken(UsernameOrEmailAlreadyTaken ex)
    {
        String message = ex.getMessage();
        return new ResponseEntity<String>(message, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(RoleDoesNotExistException.class)
    public ResponseEntity<String> handleRoleDoesNotExistException()
    {
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }


    @PostMapping("/register")
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
            // Attempt to login the user
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



    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody LoginObject lo) {

        System.out.println("login object from map:");
        System.out.println(lo.getUsername());
        System.out.println(lo.getPassword());

        return authService.loginUser(lo.getUsername(), lo.getPassword());
    }




}
