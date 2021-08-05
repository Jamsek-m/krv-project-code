package com.mjamsek.auth.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ConfigBundle("config.admin.ui")
@ApplicationScoped
public class AdminUiConfig {
    
    @ConfigValue("url")
    private String adminUiUrl;
    
    public String getAdminUiUrl() {
        return adminUiUrl;
    }
    
    public void setAdminUiUrl(String adminUiUrl) {
        this.adminUiUrl = adminUiUrl;
    }
}
