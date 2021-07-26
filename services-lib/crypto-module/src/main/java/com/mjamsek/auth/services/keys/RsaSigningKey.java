package com.mjamsek.auth.services.keys;

import com.mjamsek.auth.lib.enums.KeyType;
import com.mjamsek.auth.services.utils.KeyUtil;
import com.mjamsek.rest.exceptions.RestException;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaSigningKey extends SigningKey {
    
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    
    public RsaSigningKey(SignatureAlgorithm algorithm, String kid, String privateKey, String publicKey) {
        super(algorithm, KeyType.RSA, kid);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(KeyUtil.stringifiedKeyToBytes(privateKey)));
            this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(KeyUtil.stringifiedKeyToBytes(publicKey)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RestException(e.getMessage());
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
