package com.mjamsek.auth.services.keys;

import com.mjamsek.auth.lib.enums.KeyType;
import com.mjamsek.auth.services.utils.KeyUtil;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ECSigningKey extends SigningKey {
    
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    
    public ECSigningKey(SignatureAlgorithm algorithm, String kid, String privateKey, String publicKey) {
        super(algorithm, KeyType.ELLIPTIC_CURVE, kid);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            this.publicKey = KeyUtil.loadPublicKey(publicKey, keyFactory);
            this.privateKey = KeyUtil.loadPrivateKey(privateKey, keyFactory);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Invalid algorithm!");
        }
    }
    
    @Override
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
    
    @Override
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
}
