package com.example.springbootdemo.jwt;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/6/4 15:20
 */

@Configuration
public class JwtConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        InterceptorRegistration registration = registry.addInterceptor(new JwtInterceptor());
        registration.addPathPatterns("/**");
        registration.excludePathPatterns("/user/login","/error");
    }
}
