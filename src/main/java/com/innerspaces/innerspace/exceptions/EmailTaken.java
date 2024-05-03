package com.innerspaces.innerspace.exceptions;

import java.io.Serial;
import java.util.List;

public class EmailTaken extends RuntimeException{
    @Serial
    private static  final long serialVersionUID = 1L;

    public EmailTaken(String message)
    {
        super("An account already exist with email: " + message);
    }
    public EmailTaken(List<String> message, String username)
    {
        super("An account already exist with the username " + username + ", " + "try these instead: " + message);
    }
}
