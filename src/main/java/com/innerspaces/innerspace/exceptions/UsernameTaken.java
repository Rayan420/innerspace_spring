package com.innerspaces.innerspace.exceptions;

import java.io.Serial;
import java.util.List;

public class UsernameTaken  extends RuntimeException {

    @Serial
    private static  final long serialVersionUID = 1L;

    public UsernameTaken(String message, List<String> names)
    {
        super(message + names);
    }
}
