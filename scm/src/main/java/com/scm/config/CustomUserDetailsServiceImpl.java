package com.scm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scm.repositories.UserRepository;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(CustomUserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads the user by their username (email in this case) for authentication
     * purposes.
     * 
     * This method searches the user repository for a user with the specified email.
     * If a user is found, it returns the user's details; otherwise, it throws
     * a UsernameNotFoundException.
     *
     * Implements the UserDetailsService interface.
     *
     * @param username the email of the user to be loaded
     * @return UserDetails containing the user's information
     * @throws UsernameNotFoundException if no user is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by email: {}", username);

        return userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", username);
                    return new UsernameNotFoundException("User not found with email: " + username);
                });
    }

}
