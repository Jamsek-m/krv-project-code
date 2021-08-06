package com.mjamsek.auth.lib.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ClientType {
    @JsonProperty("PUBLIC")
    PUBLIC,
    @JsonProperty("BEARER_ONLY")
    BEARER_ONLY,
    @JsonProperty("CONFIDENTIAL")
    CONFIDENTIAL,
}
