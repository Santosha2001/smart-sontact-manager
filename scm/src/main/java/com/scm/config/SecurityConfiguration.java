package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Autowired
    CustomUserDetailsServiceImpl customUserDetailsServiceImpl;

    @Autowired
    private OAuthAuthenicationSuccessHandler handler;

    @Autowired
    private AuthFailtureHandler authFailtureHandler;

    /**
     * Configures and returns a PasswordEncoder bean.
     * 
     * This method initializes and configures a BCryptPasswordEncoder,
     * which is used for hashing passwords. BCrypt is a strong hashing
     * algorithm designed to ensure passwords are stored securely.
     * 
     * @return a configured BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures and returns a DaoAuthenticationProvider bean.
     *
     * This method sets up a DaoAuthenticationProvider with the custom
     * UserDetailsService
     * implementation and password encoder. This configuration is essential for
     * managing authentication using DAO-based user details and password encoding.
     *
     * @return a configured DaoAuthenticationProvider instance
     */

    /**
     * Configures and returns a DaoAuthenticationProvider bean.
     * 
     * This method sets up a DaoAuthenticationProvider with a custom
     * UserDetailsService
     * implementation and a password encoder. This configuration is essential for
     * managing authentication using DAO-based user details and password encoding.
     * 
     * @return a configured DaoAuthenticationProvider instance
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsServiceImpl);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    /**
     * Configures and returns a SecurityFilterChain bean.
     * 
     * This method sets up the security configurations for the application,
     * including
     * request authorization, form-based login, CSRF protection, OAuth2 login, and
     * logout
     * configurations.
     * 
     * @param httpSecurity the HttpSecurity object to configure
     * @return a configured SecurityFilterChain instance
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
        });

        httpSecurity.formLogin(formLogin -> {

            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/authenticate");
            formLogin.successForwardUrl("/user/profile");
            // formLogin.failureForwardUrl("/login?error=true");
            // formLogin.defaultSuccessUrl("/home");
            formLogin.usernameParameter("email");
            formLogin.passwordParameter("password");

            formLogin.failureHandler(authFailtureHandler);
        });

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // oauth configurations
        httpSecurity.oauth2Login(oauth -> {
            oauth.loginPage("/login");
            oauth.successHandler(handler);
        });

        httpSecurity.logout(logoutForm -> {
            logoutForm.logoutUrl("/do-logout");
            logoutForm.logoutSuccessUrl("/login?logout=true");
        });

        return httpSecurity.build();

    }

}
