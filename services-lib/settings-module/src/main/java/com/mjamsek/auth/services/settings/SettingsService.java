package com.mjamsek.auth.services.settings;

import com.mjamsek.auth.lib.Settings;

import java.util.List;
import java.util.Map;

public interface SettingsService {
    
    Map<String, Settings> getSettings(List<String> keys);
    
}
