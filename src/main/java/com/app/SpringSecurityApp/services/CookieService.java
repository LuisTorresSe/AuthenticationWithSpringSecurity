package com.app.SpringSecurityApp.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Value("${security_jwt_refresh_token_expiration}")
    private Integer expiration_refreshToken;

    public Cookie createCookieToken(String name, String token) {
        Cookie cookie = new Cookie(name,token);
        cookie.setPath("/");
        cookie.setMaxAge(expiration_refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }


    public void addCookieToResponse(HttpServletResponse response, Cookie... cookie)
    {
        for(Cookie c : cookie){
            response.addCookie(c);
        }
    }

}
