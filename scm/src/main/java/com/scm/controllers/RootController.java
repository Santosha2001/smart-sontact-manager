package com.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.entities.User;
import com.scm.services.UserService;
import com.scm.utils.Helper;

@ControllerAdvice
public class RootController {

    /**
     * @ControllerAdvice is a specialized component in Spring that allows for
     *                   global exception handling, model attributes handling, and
     *                   data binding
     *                   across multiple controllers. It helps separate
     *                   error-handling logic from
     *                   the controller logic, promoting cleaner code and better
     *                   separation of concerns.
     */

    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    @Autowired
    private UserService userService;

    /**
     * Adds logged-in user information to the model for the current request.
     * 
     * This method performs the following tasks:
     * 1. Checks if the authentication object is null; if so, it returns
     * immediately.
     * 2. Retrieves the email of the logged-in user from the authentication object.
     * 3. Logs the username of the logged-in user.
     * 4. Fetches the user data from the database using the retrieved email.
     * 5. Adds the retrieved user information to the model.
     *
     * @param model          the model to which the user information will be added
     * @param authentication the authentication object containing the user's
     *                       authentication information
     */
    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {
        if (authentication == null) {
            return;
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        logger.info("User logged in: {}", username);

        User user = userService.getUserByUserEmail(username);

        model.addAttribute("loggedInUser", user);

    }
}
