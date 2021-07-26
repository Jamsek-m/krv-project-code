package com.mjamsek.auth.services.keys;

import com.mjamsek.auth.lib.enums.KeyType;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bouncycastle.jce.ECNamedCurveTable;

public class ECSigningKey extends SigningKey {
    
    public ECSigningKey(SignatureAlgorithm algorithm, String kid) {
        super(algorithm, KeyType.ELLIPTIC_CURVE, kid);
        var pspec = ECNamedCurveTable.getParameterSpec("");
        
        
    }
    
}
