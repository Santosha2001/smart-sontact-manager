package com.scm.services;

import java.util.List;
import java.util.Optional;

import com.scm.entities.User;

public interface UserService {

    User saveUser(User user);

    Optional<User> getUserByUserId(String id);

    Optional<User> updateUser(User user);

    void deleteUserByUserId(String id);

    boolean isUserExistByUserId(String userId);

    boolean isUserExistByUserEmail(String email);

    List<User> getAllUsers();

    User getUserByUserEmail(String email);

}
