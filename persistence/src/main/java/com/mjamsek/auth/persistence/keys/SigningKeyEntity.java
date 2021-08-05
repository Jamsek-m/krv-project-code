package com.mjamsek.auth.persistence.keys;

import com.mjamsek.auth.lib.enums.KeyType;
import com.mjamsek.auth.persistence.BaseEntity;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "key_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "signing_keys", indexes = {
    @Index(name = "IDX_SIGN_KEYS_ALG_UNIQUE", columnList = "algorithm", unique = true)
})
@NamedQueries({
    @NamedQuery(name = SigningKeyEntity.GET_BY_ALG, query = "SELECT k FROM SigningKeyEntity k WHERE k.algorithm = :algorithm"),
    @NamedQuery(name = SigningKeyEntity.GET_DEFAULT_KEYS_SORTED, query = "SELECT k FROM SigningKeyEntity k ORDER BY k.priority DESC"),
    @NamedQuery(name = SigningKeyEntity.CHECK_EXISTING_ALG, query = "SELECT CASE WHEN (COUNT(k) > 0) THEN true ELSE false END FROM SigningKeyEntity k WHERE k.algorithm = :algorithm")
})
public abstract class SigningKeyEntity extends BaseEntity {
    public static final String GET_BY_ALG = "SigningKeyEntity.getByAlg";
    public static final String GET_DEFAULT_KEYS_SORTED = "SigningKeyEntity.getDefaultKeysSorted";
    public static final String CHECK_EXISTING_ALG = "SigningKeyEntity.checkExistingAlg";

    @Column(name = "algorithm", unique = true, updatable = false)
    @Enumerated(EnumType.STRING)
    protected SignatureAlgorithm algorithm;
    
    @Column(name= "key_type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    protected KeyType keyType;
    
    @Column(name = "priority")
    protected int priority;
    
    public SignatureAlgorithm getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(SignatureAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    public KeyType getKeyType() {
        return keyType;
    }
    
    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
