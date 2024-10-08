package com.scm.services;

import jakarta.servlet.http.HttpSession;

public interface AuthService {

    boolean verifyEmailToken(String token, HttpSession session);
}
