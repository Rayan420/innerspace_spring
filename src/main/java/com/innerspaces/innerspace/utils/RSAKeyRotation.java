package com.innerspaces.innerspace.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component

public class RSAKeyRotation {
    private final RSAKeyProperty rsaKeyProperty;

    public RSAKeyRotation(RSAKeyProperty rsaKeyProperty) {
        this.rsaKeyProperty = rsaKeyProperty;
    }

    @Scheduled(fixedDelay = 86400000) // Rotate keys every 24 hours
    public void rotateKeys() {
        KeyPair keyPair = KeyGeneratorUtility.generateKeyPair();
        rsaKeyProperty.setPrivateKey((RSAPrivateKey) keyPair.getPublic());
        rsaKeyProperty.setPublicKey((RSAPublicKey) keyPair.getPublic());
    }

}
