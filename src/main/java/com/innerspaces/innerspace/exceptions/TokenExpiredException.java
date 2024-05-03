package com.innerspaces.innerspace.exceptions;

import java.io.Serial;

public class TokenExpiredException extends RuntimeException{

    @Serial
    private static  final long serialVersionUID = 1L;

    public TokenExpiredException(String message)
    {
        super(message);
    }

}
