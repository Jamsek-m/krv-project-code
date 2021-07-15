package com.mjamsek.auth.persistence.user;

import com.mjamsek.auth.persistence.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {
    
    @Column(name = "username")
    private String username;
    
    
    
}
