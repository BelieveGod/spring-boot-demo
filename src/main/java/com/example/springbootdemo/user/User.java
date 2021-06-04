package com.example.springbootdemo.user;

import lombok.Data;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/6/4 15:49
 */
@Data
public class User {
    private Long id;
    private String userName;
    private String password;
}
