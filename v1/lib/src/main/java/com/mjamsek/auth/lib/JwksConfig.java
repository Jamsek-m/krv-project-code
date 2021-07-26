package com.mjamsek.auth.lib;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwksConfig {

    @JsonProperty("keys")
    private List<JsonWebKey> keys;
    
    public List<JsonWebKey> getKeys() {
        return keys;
    }
    
    public void setKeys(List<JsonWebKey> keys) {
        this.keys = keys;
    }
}
