package com.example.springbootdemo.common;

import lombok.Builder;
import lombok.Data;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/6/4 15:46
 */
@Data
@Builder
public class HttpResult {
    private Integer code;
    private String description;
    private Object data;
}
