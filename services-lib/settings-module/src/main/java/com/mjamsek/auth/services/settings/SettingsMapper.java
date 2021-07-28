package com.mjamsek.auth.services.settings;

import com.mjamsek.auth.lib.Settings;
import com.mjamsek.auth.persistence.settings.SettingsEntity;

import java.util.UUID;

public class SettingsMapper {
    
    public static Settings fromEntity(SettingsEntity entity) {
        Settings settings = new Settings();
        settings.setId(UUID.fromString(entity.getId()));
        settings.setTimestamp(entity.getTimestamp().toInstant());
        settings.setKey(entity.getKey());
        settings.setValue(entity.getValue());
        settings.setType(entity.getType());
        return settings;
    }
    
}
