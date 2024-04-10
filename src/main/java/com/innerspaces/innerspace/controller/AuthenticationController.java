package com.innerspaces.innerspace.controller;

import com.innerspaces.innerspace.exceptions.RoleDoesNotExistException;
import com.innerspaces.innerspace.exceptions.UsernameOrEmailAlreadyTaken;
import com.innerspaces.innerspace.models.user.ApplicationUser;
import com.innerspaces.innerspace.models.user.RegistrationObject;
import com.innerspaces.innerspace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
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
        return userService.registerUser(ro);
    }




}
