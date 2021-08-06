package com.mjamsek.auth.lib.requests;

public class RegistrationRequest {
    
    private String username;
    
    private String password;
    
    private String confirmPassword;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String avatar;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        if (username == null || username.trim().isBlank()) {
            this.username = null;
        } else {
            this.username = username;
        }
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        if (password == null || password.trim().isBlank()) {
            this.password = null;
        } else {
            this.password = password;
        }
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        if (confirmPassword == null || confirmPassword.trim().isBlank()) {
            this.confirmPassword = null;
        } else {
            this.confirmPassword = confirmPassword;
        }
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isBlank()) {
            this.firstName = null;
        } else {
            this.firstName = firstName;
        }
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isBlank()) {
            this.lastName = null;
        } else {
            this.lastName = lastName;
        }
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (email == null || email.trim().isBlank()) {
            this.email = null;
        } else {
            this.email = email;
        }
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        if (avatar == null || avatar.trim().isBlank()) {
            this.avatar = null;
        } else {
            this.avatar = avatar;
        }
    }
}
