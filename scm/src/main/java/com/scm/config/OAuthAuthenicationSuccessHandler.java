package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.repositories.UserRepository;
import com.scm.utils.ScmAppConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenicationSuccessHandler implements AuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(OAuthAuthenicationSuccessHandler.class);

    @Autowired
    private UserRepository userRepo;

    /**
     * Handles successful OAuth2 authentication.
     * 
     * This method processes OAuth2 authentication success by identifying the
     * provider,
     * extracting user information from the OAuth2 token, creating a User entity,
     * and
     * saving it to the repository if the user is new. It also redirects the user to
     * their profile page upon successful authentication.
     * 
     * @param request        the HttpServletRequest object
     * @param response       the HttpServletResponse object
     * @param authentication the Authentication object containing the user's
     *                       authentication information
     * @throws IOException      if an input or output error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        logger.info("OAuthAuthenticationSuccessHandler");

        // Identify the provider
        var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
        logger.info("Authorized Client Registration ID: {}", authorizedClientRegistrationId);

        var oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        oauthUser.getAttributes().forEach((key, value) -> {
            logger.info("{} : {}", key, value);
        });

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setRoleList(List.of(ScmAppConstants.ROLE_USER));
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setPassword("dummy");

        // Set user attributes based on the provider
        switch (authorizedClientRegistrationId.toLowerCase()) {
            case "google":
                user.setEmail(oauthUser.getAttribute("email").toString());
                user.setProfilePic(oauthUser.getAttribute("picture").toString());
                user.setName(oauthUser.getAttribute("name").toString());
                user.setProviderUserId(oauthUser.getName());
                user.setProvider(Providers.GOOGLE);
                user.setAbout("This account is created using Google.");
                break;

            case "github":
                String email = oauthUser.getAttribute("email") != null ? oauthUser.getAttribute("email").toString()
                        : oauthUser.getAttribute("login").toString() + "@gmail.com";
                String picture = oauthUser.getAttribute("avatar_url").toString();
                String name = oauthUser.getAttribute("login").toString();
                String providerUserId = oauthUser.getName();

                user.setEmail(email);
                user.setProfilePic(picture);
                user.setName(name);
                user.setProviderUserId(providerUserId);
                user.setProvider(Providers.GITHUB);
                user.setAbout("This account is created using GitHub.");
                break;

            case "linkedin":
                // Handle LinkedIn user attributes here
                break;

            default:
                logger.warn("OAuthAuthenticationSuccessHandler: Unknown provider");
                break;
        }

        User existingUser = userRepo.findByEmail(user.getEmail()).orElse(null);
        if (existingUser == null) {
            userRepo.save(user);
            logger.info("User saved: {}", user.getEmail());
        } else {
            logger.info("User already exists: {}", user.getEmail());
        }

        // Redirect to user profile
        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }

}