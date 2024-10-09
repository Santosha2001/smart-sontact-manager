package com.scm.config;

import java.io.IOException;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.scm.utils.Message;
import com.scm.utils.MessageType;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthFailtureHandler implements AuthenticationFailureHandler {

    /**
     * Handles authentication failure events.
     * This method is invoked when authentication fails, either because the user is
     * disabled or for other reasons.
     * It sets an appropriate message in the session based on the type of failure
     * and redirects the user accordingly.
     * 
     * @param request   the HttpServletRequest object
     * @param response  the HttpServletResponse object
     * @param exception the exception that caused the authentication failure
     * @throws IOException      if an input or output error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof DisabledException) {

            // user is disabled
            HttpSession session = request.getSession();
            session.setAttribute("message",
                    Message.builder()
                            .content("User is disabled, Email with  varification link is sent on your email id !!")
                            .type(MessageType.red).build());

            response.sendRedirect("/login");
        } else {
            response.sendRedirect("/login?error=true");
            // request.getRequestDispatcher("/login").forward(request, response);

        }

    }
}
