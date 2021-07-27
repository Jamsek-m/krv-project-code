package com.mjamsek.auth.services.settings;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjamsek.auth.lib.enums.SettingsValueType;

import java.util.Optional;

public interface ConfigProvider {
    
    void set(String key, SettingsValueType type, String value);
    
    void delete(String key);
    
    Optional<String> getString(String key);
    
    Optional<Integer> getInteger(String key);
    
    Optional<Double> getFloat(String key);
    
    Optional<Double> getNumber(String key);
    
    Optional<Boolean> getBoolean(String key);
    
    Optional<JsonNode> getJson(String key);
    
    
    
}
