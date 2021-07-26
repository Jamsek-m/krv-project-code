package com.mjamsek.auth.services.utils;

import com.mjamsek.auth.persistence.keys.RsaSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

public class KeyUtil {
    
    private KeyUtil() {
    
    }
    
    public static String keyToString(Key key) {
        return keyToString(key.getEncoded());
    }
    
    public static String keyToString(byte[] key) {
        return new String(Base64.getEncoder().encode(key));
    }
    
    public static byte[] stringifiedKeyToBytes(String key) {
        return Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8));
    }
    
    public static Optional<PrivateKey> entityToPrivateKey(SigningKeyEntity entity, KeyFactory keyFactory) {
        if (entity instanceof RsaSigningKeyEntity) {
            RsaSigningKeyEntity rsaKeyEntity = (RsaSigningKeyEntity) entity;
            try {
                PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(KeyUtil.stringifiedKeyToBytes(rsaKeyEntity.getPrivateKey())));
                return Optional.of(privateKey);
            } catch (InvalidKeySpecException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
    
    public static Optional<PublicKey> entityToPublicKey(SigningKeyEntity entity, KeyFactory keyFactory) {
        if (entity instanceof RsaSigningKeyEntity) {
            try {
                RsaSigningKeyEntity rsaKeyEntity = (RsaSigningKeyEntity) entity;
                byte[] pubKeyBytes = KeyUtil.stringifiedKeyToBytes(rsaKeyEntity.getPublicKey());
                var x509EncodedKeySpec = new X509EncodedKeySpec(pubKeyBytes);
                PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
                return Optional.of(publicKey);
            } catch (InvalidKeySpecException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
    
}
