package com.contactfy.config.i18n;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LocaleInterceptor implements HandlerInterceptor{
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String acceptLanguage = request.getHeader("Accept-Language");

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;
        LocaleContextHolder.setLocale(locale);

        return true;
    }
}
