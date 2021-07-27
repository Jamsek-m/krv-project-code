package com.mjamsek.auth.persistence.settings;

import com.mjamsek.auth.lib.enums.SettingsValueType;
import com.mjamsek.auth.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "settings", indexes = {
    @Index(name = "IDX_SETTINGS_KEY_SEARCH", columnList = "settings_key", unique = true)
})
@NamedQueries({
    @NamedQuery(name = SettingsEntity.GET_BY_KEY, query = "SELECT s FROM SettingsEntity s WHERE s.key = :key"),
    @NamedQuery(name = SettingsEntity.DELETE_BY_KEY, query = "DELETE FROM SettingsEntity s WHERE s.key =:key")
})
public class SettingsEntity extends BaseEntity {
    
    public static final String GET_BY_KEY = "SettingsEntity.getByKey";
    public static final String DELETE_BY_KEY = "SettingsEntity.deleteByKey";
    
    public static final String CONFIG_FILE_PREFIX = "static";
    
    @Column(name = "settings_key")
    private String key;
    
    @Column(name = "settings_value")
    private String value;
    
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SettingsValueType type;
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public SettingsValueType getType() {
        return type;
    }
    
    public void setType(SettingsValueType type) {
        this.type = type;
    }
}
