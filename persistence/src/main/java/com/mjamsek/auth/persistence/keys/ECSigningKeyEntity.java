package com.mjamsek.auth.persistence.keys;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ELLIPTIC_CURVE")
public class ECSigningKeyEntity extends AsymmetricSigningKeyEntity {

}
