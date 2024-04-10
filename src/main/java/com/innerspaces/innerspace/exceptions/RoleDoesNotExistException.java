package com.innerspaces.innerspace.exceptions;

import java.io.Serial;

public class RoleDoesNotExistException extends RuntimeException{

    @Serial
    private static  final long serialVersionUID = 1L;
    public RoleDoesNotExistException() {
        super("Role does not exist");
    }
}
