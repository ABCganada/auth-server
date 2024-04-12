package com.login.oauthAndJwt.auth.oauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        Cookie jwtCookie = new Cookie("jwtToken", null);
        jwtCookie.setMaxAge(0);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);

        try {
            response.sendRedirect("http://localhost:8000/main");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
