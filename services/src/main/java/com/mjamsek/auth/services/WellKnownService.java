package com.mjamsek.auth.services;

import com.mjamsek.auth.lib.JwksConfig;
import com.mjamsek.auth.lib.WellKnownConfig;

public interface WellKnownService {
    
    WellKnownConfig getConfig();
    
    JwksConfig getJwksConfig();
    
}
