package com.scm.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Helper {

    // @Value("${server.baseUrl}")
    private String baseUrl = "http://localhost:8080/auth/verify-email?token=2425";

    public static String getEmailOfLoggedInUser(Authentication authentication) {
        // Principal principal = (Principal) authentication.getPrincipal();

        // sign in with email and password
        if (authentication instanceof OAuth2AuthenticationToken) {
            var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            var clientId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();

            var oauth2User = (OAuth2User) authentication.getPrincipal();
            String username = "";

            if (clientId.equalsIgnoreCase("google")) {
                // sign in with google
                System.out.println("Getting emial from google");
                username = oauth2User.getAttribute("email").toString();

            } else if (clientId.equalsIgnoreCase("github")) {
                // sign in with github
                System.out.println("Getting emial from github");
                username = oauth2User.getAttribute("email") != null ? oauth2User.getAttribute("email").toString()
                        : oauth2User.getAttribute("login").toString() + "@gmail.com";
            }
            return username;

        }

        else {
            System.out.println("Getting data from database.");
            return authentication.getName();
        }

    }

    public static String getLinkForEmailVerificatiton(String emailToken) {

        // http://localhost:8080/auth/verify-email?token=2425

        // return baseUrl + "/auth/verify-email?token=" + emailToken;
        return "http://localhost:8080/auth/verify-email?token=" + emailToken;

    }
}
