package com.contactfy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.contactfy.config.i18n.LocaleInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final LocaleInterceptor localeInterceptor;

    public WebConfig(LocaleInterceptor localeInterceptor) {
	this.localeInterceptor = localeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(localeInterceptor);
    }
}
