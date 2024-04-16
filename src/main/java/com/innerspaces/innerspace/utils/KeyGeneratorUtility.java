package com.innerspaces.innerspace.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyGeneratorUtility {

    public static KeyPair generateKeyPair()
    {
        KeyPair keyPair;
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair=keyPairGenerator.generateKeyPair();
        }
        catch (Exception e)
        {
            throw new IllegalStateException();
        }
        System.out.println("public key  "+keyPair.getPublic() );
        System.out.println("private key  "+keyPair.getPrivate() );

        return keyPair;
    }

}
