package com.mjamsek.auth.lib;

import com.mjamsek.auth.lib.enums.AttributeType;

public class UserAttribute extends BaseType {
    
    private String key;
    
    private String value;
    
    private AttributeType type;
    
    private String userId;
    
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
