package com.example.springbootdemo.runnable;

import cn.hutool.core.codec.Base64Encoder;
import com.example.springbootdemo.jwt.JwtProperties;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/6/4 14:13
 */
@Component
public class JjwtQuickStart implements CommandLineRunner {
    @Autowired
    private JwtProperties jwtProperties;
    @Override
    public void run(String... args) throws Exception {
        Key key=Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.println("key.getFormat() = " + key.getEncoded());
        String encode = Base64Encoder.encode(key.getEncoded());
        String encode1 = Encoders.BASE64URL.encode(key.getEncoded());
        String encode2 = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("encode = " + encode);
        System.out.println("encode1 = " + encode1);
        System.out.println("encode2 = " + encode2);
        System.out.println("jwtProperties.getBase64UrlKey() = " + jwtProperties.getBase64UrlKey());
    }
}
