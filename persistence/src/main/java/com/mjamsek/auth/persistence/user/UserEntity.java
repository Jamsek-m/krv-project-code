package com.mjamsek.auth.persistence.user;

import com.mjamsek.auth.persistence.BaseEntity;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "IDX_USERS_USERNAME_UNIQUE", columnList = "username", unique = true)
})
@NamedQueries({
    @NamedQuery(name = UserEntity.GET_BY_USERNAME, query = "SELECT u FROM UserEntity u WHERE u.username = :username")
})
public class UserEntity extends BaseEntity {
    
    public static final String GET_BY_USERNAME = "UserEntity.getByUsername";
    
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<UserAttributeEntity> attributes;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<UserCredentialsEntity> credentials;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public List<UserAttributeEntity> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(List<UserAttributeEntity> attributes) {
        this.attributes = attributes;
    }
    
    public List<UserCredentialsEntity> getCredentials() {
        return credentials;
    }
    
    public void setCredentials(List<UserCredentialsEntity> credentials) {
        this.credentials = credentials;
    }
}
