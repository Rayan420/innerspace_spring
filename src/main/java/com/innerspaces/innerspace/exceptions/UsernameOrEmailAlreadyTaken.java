package com.innerspaces.innerspace.exceptions;

import java.util.List;

public class UsernameOrEmailAlreadyTaken extends RuntimeException{
    private static  final long serialVersionUID = 1L;

    public UsernameOrEmailAlreadyTaken(String message)
    {
        super("An account already exist with email: " + message);
    }
    public UsernameOrEmailAlreadyTaken(List<String> message, String username)
    {
        super("An account already exist with the username " + username + ", " + "try these instead: " + message);
    }
}
