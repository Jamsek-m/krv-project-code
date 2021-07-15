package com.mjamsek.auth.persistence.client;

import com.mjamsek.auth.persistence.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "clients")
public class ClientEntity extends BaseEntity {
    
    @Column(name = "name")
    private String name;
    
    
    
}
