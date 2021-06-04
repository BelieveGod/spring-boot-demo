package com.example.springbootdemo.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/6/4 15:20
 */
@Component
@ConfigurationProperties(prefix = "jjwt")
@Data
public class JwtProperties {

    public static final String HEADER = "Authorization";
    private String base64UrlKey;
}
