package com.example.springbootdemo.user;

import com.example.springbootdemo.common.HttpResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/6/4 15:43
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @PostMapping("/login")
    public HttpResult login(@RequestBody User user){
        System.out.println("user.getUserName() = " + user.getUserName());
        System.out.println(user.getPassword());

        return  HttpResult.builder().code(200).description("login success").build();
    }

    @GetMapping("")
    public HttpResult getUser(){
        User user = new User();
        user.setUserName("admin");
        user.setId(1L);
        return HttpResult.builder().code(200).description("success").data(user).build();
    }

}
