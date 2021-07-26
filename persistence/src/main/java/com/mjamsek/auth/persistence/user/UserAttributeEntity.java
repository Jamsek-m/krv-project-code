package com.mjamsek.auth.persistence.user;

import com.mjamsek.auth.lib.enums.AttributeType;
import com.mjamsek.auth.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "user_attributes", indexes = {
    @Index(name = "IDX_USER_ATTRS_USER_FK", columnList = "user_id"),
})
public class UserAttributeEntity extends BaseEntity {
    
    @Column(name = "attr_key")
    private String key;
    
    @Column(name = "attr_value")
    private String value;
    
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private AttributeType type;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public AttributeType getType() {
        return type;
    }
    
    public void setType(AttributeType type) {
        this.type = type;
    }
    
    public UserEntity getUser() {
        return user;
    }
    
    public void setUser(UserEntity user) {
        this.user = user;
    }
}
