package com.scm.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.exceptions.ResourceNotFoundException;
import com.scm.forms.UserForm;
import com.scm.repositories.UserRepository;
import com.scm.services.EmailService;
import com.scm.services.UserService;
import com.scm.utils.Helper;
import com.scm.utils.ScmAppConstants;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Converts a UserForm object to a User entity.
     * 
     * This method creates a new User object and populates its fields with the data
     * from the provided UserForm. The user's enabled status is set to false and a
     * default profile picture URL is assigned.
     *
     * @param userForm the UserForm object containing user details for conversion
     * @return a User entity populated with data from the UserForm
     */
    @Override
    public User convertUserFormToUser(UserForm userForm) {
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setEnabled(false);
        user.setProfilePic(
                "https://png.pngtree.com/element_our/20200610/ourmid/pngtree-character-default-avatar-image_2237203.jpg");
        return user;
    }

    /**
     * Saves a new user to the repository with encoded password and email
     * verification token.
     * 
     * This method performs the following tasks:
     * 1. Generates a unique user ID and sets it to the user.
     * 2. Encodes the user's password and sets it.
     * 3. Sets the user's roles.
     * 4. Generates an email verification token and sets it to the user.
     * 5. Saves the user to the repository.
     * 6. Sends a verification email to the user with a link to verify their
     * account.
     * 
     * @param user the User object to be saved
     * @return the saved User object
     */
    @Override
    public User saveUser(User user) {
        // Check if the email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            // Instead of throwing an exception, return null or use a custom response
            return null;
        }

        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleList(List.of(ScmAppConstants.ROLE_USER));
        String emailToken = UUID.randomUUID().toString();
        user.setEmailToken(emailToken);
        User savedUser = userRepository.save(user);
        String emailLink = Helper.getLinkForEmailVerificatiton(emailToken);
        emailService.sendEmail(savedUser.getEmail(), "Verify Account: Smart Contact Manager", emailLink);
        return savedUser;
    }

    /**
     * Retrieves a user by their user ID.
     * 
     * This method searches the user repository for a user with the specified ID.
     * If a user is found, it is returned wrapped in an Optional; otherwise, an
     * empty Optional is returned.
     *
     * @param id the ID of the user to be retrieved
     * @return an Optional containing the User if found, or an empty Optional if not
     *         found
     */
    @Override
    public Optional<User> getUserByUserId(String id) {
        return userRepository.getUserById(id);
    }

    /**
     * Updates an existing user with new information.
     * 
     * This method performs the following tasks:
     * 1. Retrieves the existing user by their user ID.
     * 2. Updates the old user's fields with the new information provided in the
     * user parameter.
     * 3. Saves the updated user back to the repository.
     * 4. Returns an Optional containing the saved User object.
     *
     * @param user the User object containing updated information
     * @return an Optional containing the updated User object, or empty if not found
     * @throws ResourceNotFoundException if the user with the specified ID is not
     *                                   found
     */
    @Override
    public Optional<User> updateUser(User user) {

        User oldUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // update karenge oldUser from user
        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        oldUser.setPassword(user.getPassword());
        oldUser.setAbout(user.getAbout());
        oldUser.setPhoneNumber(user.getPhoneNumber());
        oldUser.setProfilePic(user.getProfilePic());
        oldUser.setEnabled(user.isEnabled());
        oldUser.setEmailVerified(user.isEmailVerified());
        oldUser.setPhoneVerified(user.isPhoneVerified());
        oldUser.setProvider(user.getProvider());
        oldUser.setProviderUserId(user.getProviderUserId());

        // save the user in database
        User save = userRepository.save(oldUser);
        return Optional.ofNullable(save);

    }

    /**
     * Deletes a user by their user ID.
     * 
     * This method retrieves a user from the repository using their ID.
     * If the user is found, they are deleted from the repository.
     * If the user is not found, a ResourceNotFoundException is thrown.
     *
     * @param id the ID of the user to be deleted
     * @throws ResourceNotFoundException if no user is found with the given ID
     */
    @Override
    public void deleteUserByUserId(String id) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(oldUser);

    }

    /**
     * Checks if a user exists by their user ID.
     * 
     * This method searches the user repository for a user with the specified ID.
     * If a user is found, it returns true; otherwise, it returns false.
     *
     * @param userId the ID of the user to check for existence
     * @return true if the user exists, false otherwise
     */
    @Override
    public boolean isUserExistByUserId(String userId) {
        User oldUser = userRepository.findById(userId).orElse(null);
        return oldUser != null ? true : false;
    }

    @Override
    public boolean isUserExistByUserEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByUserEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);

    }

}
