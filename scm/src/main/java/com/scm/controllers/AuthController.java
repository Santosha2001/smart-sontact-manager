package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.services.AuthService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Handles GET requests to verify a user's email using a token.
     * This method delegates the email verification to the AuthService and sets the
     * view
     * name based on the verification result.
     * 
     * @param token   the verification token sent to the user's email
     * @param session the current HTTP session to set messages
     * @return the view name to display based on the verification result:
     *         "success_page" if verification is successful,
     *         otherwise "error_page"
     */
    @GetMapping("/verify-email")
    public String verifyEmail(
            @RequestParam("token") String token, HttpSession session) {

        boolean isVerified = authService.verifyEmailToken(token, session);

        if (isVerified) {
            return "success_page";
        } else {
            return "error_page";
        }
    }
}
