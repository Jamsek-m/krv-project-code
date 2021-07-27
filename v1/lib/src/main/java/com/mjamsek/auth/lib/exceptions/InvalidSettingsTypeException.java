package com.mjamsek.auth.lib.exceptions;

import com.mjamsek.auth.lib.enums.SettingsValueType;
import com.mjamsek.rest.exceptions.RestException;

public class InvalidSettingsTypeException extends RestException {
    
    private final String key;
    
    private final SettingsValueType requiredType;
    
    public InvalidSettingsTypeException(String code, String key, SettingsValueType requiredType) {
        super(code);
        this.requiredType = requiredType;
        this.key = key;
    }
    
    public String getKey() {
        return key;
    }
    
    public SettingsValueType getRequiredType() {
        return requiredType;
    }
}
