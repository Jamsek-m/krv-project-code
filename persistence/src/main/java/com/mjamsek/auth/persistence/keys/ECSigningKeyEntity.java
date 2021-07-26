package com.mjamsek.auth.persistence.keys;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ELLIPTIC_CURVE")
public class ECSigningKeyEntity extends SigningKeyEntity {
    
    @Column(name = "curve")
    private String curve;
    
    @Column(name = "point_on_curve_x")
    private String x;
    
    @Column(name = "point_on_curve_y")
    private String y;
    
    public String getCurve() {
        return curve;
    }
    
    public void setCurve(String curve) {
        this.curve = curve;
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
