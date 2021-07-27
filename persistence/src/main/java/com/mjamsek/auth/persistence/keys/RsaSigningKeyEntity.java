package com.mjamsek.auth.persistence.keys;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("RSA")
public class RsaSigningKeyEntity extends AsymmetricSigningKeyEntity {

}
