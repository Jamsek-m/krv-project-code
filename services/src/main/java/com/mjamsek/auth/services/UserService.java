package com.mjamsek.auth.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.auth.lib.User;
import com.mjamsek.auth.lib.enums.ErrorCode;
import com.mjamsek.auth.lib.requests.RegistrationRequest;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.rest.dto.EntityList;

import java.util.Optional;

public interface UserService {
    
    EntityList<User> getUsers(QueryParameters queryParameters);
    
    User getUser(String userId);
    
    User createUser(User user);
    
    User patchUser(String userId, User user);
    
    Optional<ErrorCode> registerUser(RegistrationRequest request);
    
    Optional<UserEntity> getUserEntityById(String userId);
    
    Optional<UserEntity> getUserEntityByUsername(String username);
}
