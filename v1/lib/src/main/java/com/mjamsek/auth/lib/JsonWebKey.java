package com.mjamsek.auth.lib;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonWebKey {

    @JsonProperty("kid")
    private String kid;
    
    @JsonProperty("kty")
    private String kty;
    
    @JsonProperty("alg")
    private SignatureAlgorithm alg;
    
    @JsonProperty("use")
    private String use;
    
    @JsonProperty("x5c")
    private List<String> x5c;
    
    @JsonProperty("x5t")
    private String x5t;
    
    @JsonProperty("n")
    private String n;
    
    @JsonProperty("e")
    private String e;
    
    @JsonProperty("crv")
    private String crv;
    
    @JsonProperty("x")
    private String x;
    
    @JsonProperty("y")
    private String y;
    
    public String getKid() {
        return kid;
    }
    
    public void setKid(String kid) {
        this.kid = kid;
    }
    
    public String getKty() {
        return kty;
    }
    
    public void setKty(String kty) {
        this.kty = kty;
    }
    
    public SignatureAlgorithm getAlg() {
        return alg;
    }
    
    public void setAlg(SignatureAlgorithm alg) {
        this.alg = alg;
    }
    
    public String getUse() {
        return use;
    }
    
    public void setUse(String use) {
        this.use = use;
    }
    
    public List<String> getX5c() {
        return x5c;
    }
    
    public void setX5c(List<String> x5c) {
        this.x5c = x5c;
    }
    
    public String getX5t() {
        return x5t;
    }
    
    public void setX5t(String x5t) {
        this.x5t = x5t;
    }
    
    public String getN() {
        return n;
    }
    
    public void setN(String n) {
        this.n = n;
    }
    
    public String getE() {
        return e;
    }
    
    public void setE(String e) {
        this.e = e;
    }
    
    public String getCrv() {
        return crv;
    }
    
    public void setCrv(String crv) {
        this.crv = crv;
    }
    
    public String getX() {
        return x;
    }
    
    public void setX(String x) {
        this.x = x;
    }
    
    public String getY() {
        return y;
    }
    
    public void setY(String y) {
        this.y = y;
    }
}
