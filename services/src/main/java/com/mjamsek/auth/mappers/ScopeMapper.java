package com.mjamsek.auth.mappers;

import com.mjamsek.auth.lib.wrappers.TranslatedScope;

public class ScopeMapper {
    
    public static TranslatedScope translateScope(String scope) {
        return new TranslatedScope(scope);
    }
    
}
