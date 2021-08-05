package com.mjamsek.auth.lib.responses;

public class AnalyticsOverview {
    
    private long usersCount;
    
    private long clientsCount;
    
    private long rolesCount;
    
    private long keysCount;
    
    public long getUsersCount() {
        return usersCount;
    }
    
    public void setUsersCount(long usersCount) {
        this.usersCount = usersCount;
    }
    
    public long getClientsCount() {
        return clientsCount;
    }
    
    public void setClientsCount(long clientsCount) {
        this.clientsCount = clientsCount;
    }
    
    public long getRolesCount() {
        return rolesCount;
    }
    
    public void setRolesCount(long rolesCount) {
        this.rolesCount = rolesCount;
    }
    
    public long getKeysCount() {
        return keysCount;
    }
    
    public void setKeysCount(long keysCount) {
        this.keysCount = keysCount;
    }
}
