package com.innerspaces.innerspace.controller;

import com.innerspaces.innerspace.exceptions.RoleDoesNotExistException;
import com.innerspaces.innerspace.exceptions.UsernameOrEmailAlreadyTaken;
import com.innerspaces.innerspace.models.auth.LoginObject;
import com.innerspaces.innerspace.models.user.ApplicationUser;
import com.innerspaces.innerspace.models.user.LoginResponseDTO;
import com.innerspaces.innerspace.models.user.RegistrationObject;
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
    public ApplicationUser registerUser(@RequestBody RegistrationObject ro) throws Exception {
        return authService.registerUser(ro);
    }

    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody LoginObject lo) {

        System.out.println("login object from map:");
        System.out.println(lo.getUsername());
        System.out.println(lo.getPassword());

        return authService.loginUser(lo.getUsername(), lo.getPassword());
    }




}
