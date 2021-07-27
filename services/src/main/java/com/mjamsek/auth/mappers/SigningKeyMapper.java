package com.mjamsek.auth.mappers;

import com.mjamsek.auth.lib.JsonWebKey;
import com.mjamsek.auth.persistence.keys.ECSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.RsaSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;
import com.mjamsek.auth.services.utils.KeyUtil;
import com.nimbusds.jose.jwk.*;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

public class SigningKeyMapper {
    
    public static JsonWebKey fromEntityToJwk(SigningKeyEntity entity) {
        JsonWebKey jwk = new JsonWebKey();
        
        jwk.setKid(entity.getId());
        jwk.setAlg(entity.getAlgorithm());
        jwk.setKty(entity.getKeyType().type());
        jwk.setUse("sig");
        
        if (entity instanceof RsaSigningKeyEntity) {
            RsaSigningKeyEntity rsaKey = (RsaSigningKeyEntity) entity;
            
            JWK rsaJwk = new RSAKey.Builder(getRSAPublicKey(rsaKey.getPublicKey()))
                .keyID(rsaKey.getId())
                .keyUse(KeyUse.SIGNATURE)
                .build();
    
            Map<String, ?> keyParams = rsaJwk.getRequiredParams();
            jwk.setN((String) keyParams.get("n"));
            jwk.setE((String) keyParams.get("e"));
            
        } else if (entity instanceof ECSigningKeyEntity) {
            ECSigningKeyEntity ecKey = (ECSigningKeyEntity) entity;
            
            Curve curve = algorithmToCurve(ecKey.getAlgorithm());
            JWK ecJwk = new ECKey.Builder(curve, getECPublicKey(ecKey.getPublicKey()))
                .keyID(ecKey.getId())
                .keyUse(KeyUse.SIGNATURE)
                .build();
            
            Map<String, ?> keyParams = ecJwk.getRequiredParams();
            jwk.setCrv((String) keyParams.get("crv"));
            jwk.setX((String) keyParams.get("x"));
            jwk.setY((String) keyParams.get("y"));
        }
        
        return jwk;
    }
    
    private static ECPublicKey getECPublicKey(String pubKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return (ECPublicKey) KeyUtil.loadPublicKey(pubKey, keyFactory);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unrecognized algorithm!");
        }
    }
    
    private static RSAPublicKey getRSAPublicKey(String pubKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) KeyUtil.loadPublicKey(pubKey, keyFactory);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unrecognized algorithm!");
        }
    }
    
    private static Curve algorithmToCurve(SignatureAlgorithm algorithm) {
        if (algorithm.isEllipticCurve()) {
            switch (algorithm) {
                case ES256:
                    return Curve.P_256;
                case ES384:
                    return Curve.P_384;
                case ES512:
                    return Curve.P_521;
            }
        }
        throw new IllegalArgumentException("Invalid curve algorithm!");
    }
    
}
