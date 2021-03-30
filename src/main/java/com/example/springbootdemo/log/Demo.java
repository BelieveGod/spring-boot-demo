package com.example.springbootdemo.log;

import cn.hutool.core.io.FileUtil;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/3/19 16:38
 */
public class Demo {
    public static void main(String[] args) {
        List<String> strings = FileUtil.readUtf8Lines("C:\\Users\\92416\\Desktop\\2021-03-19-can.log");

//        for (String string : strings) {
//            if(string.startsWith("四轮转角反馈")){
//                String[] split = string.split(":");
//                String nodeID = split[1].substring(7, 8);
//                String position = split[1].substring(16, 24);
//
//                System.out.println(String.format("驱动器%s:%s", nodeID, position));
//                if (position.startsWith("1")) {
//                    System.out.println(String.format("负数的 驱动器%s:%s", nodeID, position));
//                }
//            }
//        }

        for (String string : strings) {
            if(string.startsWith("四轮转角反馈")){
                String[] split = string.split(":");
                String nodeID = split[1].substring(7, 8);
                String angulars = split[1].substring(8, 24);

                for(int i=0;i<4;i++){
                    String angular = angulars.substring(4 * i, 4 * i + 4);
                    if(Integer.parseInt(angular.substring(0,1),16)>8){
                        System.out.println(String.format("负数 %d 号论:%s", i + 5, angular));
                    }
                    System.out.println(String.format("%d 号论:%s", i + 5, angular));
                }
            }
        }
    }
}
