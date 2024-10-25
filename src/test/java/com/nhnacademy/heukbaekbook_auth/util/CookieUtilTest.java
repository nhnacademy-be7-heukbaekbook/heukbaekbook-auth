package com.nhnacademy.heukbaekbook_auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CookieUtilTest {

    @Test
    void testCreateCookie() {
        String name = "testCookie";
        String value = "testValue";
        long maxAge = 60000;

        Cookie cookie = CookieUtil.createCookie(name, value, maxAge);

        assertEquals(name, cookie.getName());
        assertEquals(value, cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.getSecure());
        assertEquals("/", cookie.getPath());
        assertEquals((int) (maxAge / 1000), cookie.getMaxAge());
    }

    @Test
    void testGetCookie_WhenCookieExists() {
        String cookieName = "testCookie";
        String cookieValue = "testValue";
        Cookie[] cookies = { new Cookie(cookieName, cookieValue) };

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(cookies);

        Optional<Cookie> result = CookieUtil.getCookie(request, cookieName);

        assertTrue(result.isPresent());
        assertEquals(cookieName, result.get().getName());
        assertEquals(cookieValue, result.get().getValue());
    }

    @Test
    void testGetCookie_WhenCookieDoesNotExist() {
        String cookieName = "nonExistentCookie";
        Cookie[] cookies = { new Cookie("otherCookie", "something") };

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(cookies);

        Optional<Cookie> result = CookieUtil.getCookie(request, cookieName);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetCookie_WhenRequestHasNoCookies() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        Optional<Cookie> result = CookieUtil.getCookie(request, "anyCookie");

        assertFalse(result.isPresent());
    }
}
