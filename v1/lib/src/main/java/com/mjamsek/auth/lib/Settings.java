package com.mjamsek.auth.lib;

import com.mjamsek.auth.lib.enums.SettingsValueType;

public class Settings extends BaseType {
    
    private String key;
    
    private String value;
    
    private SettingsValueType type;
    
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
    
    public SettingsValueType getType() {
        return type;
    }
    
    public void setType(SettingsValueType type) {
        this.type = type;
    }
}
