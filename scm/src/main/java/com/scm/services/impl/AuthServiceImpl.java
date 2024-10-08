package com.scm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.repositories.UserRepository;
import com.scm.services.AuthService;
import com.scm.utils.Message;
import com.scm.utils.MessageType;

import jakarta.servlet.http.HttpSession;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepo;

    /**
     * Verifies the email token associated with a user.
     * This method checks if the provided token matches the token stored for the
     * user
     * in the repository. If a match is found, the user's email is marked as
     * verified,
     * and the user is enabled. Messages indicating success or failure of the
     * verification process are set in the session.
     * 
     * @param token   the verification token sent to the user's email
     * @param session the current HTTP session to set messages
     * @return true if the email is successfully verified, false otherwise
     */
    @Override
    public boolean verifyEmailToken(String token, HttpSession session) {
        User user = userRepo.findByEmailToken(token).orElse(null);

        if (user != null) {
            if (user.getEmailToken().equals(token)) {
                user.setEmailVerified(true);
                user.setEnabled(true);
                userRepo.save(user);
                session.setAttribute("message", Message.builder()
                        .type(MessageType.green)
                        .content("Your email is verified. Now you can login.")
                        .build());
                return true;
            }
            session.setAttribute("message", Message.builder()
                    .type(MessageType.red)
                    .content("Email not verified! Token is not associated with user.")
                    .build());
            return false;
        }

        session.setAttribute("message", Message.builder()
                .type(MessageType.red)
                .content("Email not verified! Token is not associated with user.")
                .build());
        return false;
    }
}
