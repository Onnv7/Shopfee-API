package com.hcmute.shopfee.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public class CookieUtils {
    public static String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null; // Không tìm thấy cookie refreshToken trong request
    }

    public static HttpHeaders setRefreshTokenCookie(String refreshToken, Long maxAge) {
        HttpHeaders headers = new HttpHeaders();

        // TODO: kiem tra expire coookie
        headers.add(HttpHeaders.SET_COOKIE, "refreshToken=" + refreshToken + "; Max-Age=" + maxAge.toString() + "; Path=/; Secure; HttpOnly");
        return headers;
    }
}
