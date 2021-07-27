package com.mjamsek.auth.mappers;

import com.mjamsek.auth.lib.JsonWebKey;
import com.mjamsek.auth.persistence.keys.ECSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.RsaSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;
import com.mjamsek.auth.services.utils.KeyUtil;
import com.mjamsek.rest.exceptions.RestException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
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
            byte[] publicKeyBytes = KeyUtil.stringifiedKeyToBytes(rsaKey.getPublicKey());
            byte[] x5c = new X509EncodedKeySpec(publicKeyBytes).getEncoded();
            String x5cStringified = KeyUtil.keyToString(x5c);
            jwk.setX5c(List.of(x5cStringified));
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
            
            byte[] pubKeyBytes = KeyUtil.stringifiedKeyToBytes(pubKey);
            var x509EncodedKeySpec = new X509EncodedKeySpec(pubKeyBytes);
            return (ECPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RestException("error.server");
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
