package com.mjamsek.auth.lib.wrappers;

import static com.mjamsek.auth.lib.constants.ScopeConstants.*;

public class TranslatedScope {
    
    private final String name;
    
    private final String displayName;
    
    public static String userFriendlyScopeName(String scope) {
        switch (scope) {
            case EMAIL_SCOPE:
                return "Email address";
            case PROFILE_SCOPE:
                return "User profile information (name, username, photo)";
            case OFFLINE_ACCESS_SCOPE:
                return "Perform offline actions on behalf of user (used for automating tasks)";
            default:
                return scope;
        }
    }
    
    public TranslatedScope(String name) {
        this.name = name;
        this.displayName = TranslatedScope.userFriendlyScopeName(name);
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isTranslated() {
        return !this.name.equals(this.displayName);
    }
}
