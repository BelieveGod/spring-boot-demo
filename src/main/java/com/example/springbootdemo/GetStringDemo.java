package com.example.springbootdemo;

import com.example.springbootdemo.serial.utlil.HexUtils;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/3/30 16:58
 */
public class GetStringDemo {
    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();
        builder.append("6162616261620000")
               .append("0000000000000000")
               .append("6162616261626100")
               .append("0000000000000000")
               .append("52414e4745524d49")
               .append("4e49000000000000")
               .append("6162616261000000")
               .append("0000000061626162")
               .append("6162000061626163")
               .append("0000000031000000");
        byte[] bytes = HexUtils.hexString2bytes(builder.toString());
        String s = new String(bytes);
        System.out.println("s = " + s);

        StringBuilder builder2 = new StringBuilder();
        builder2.append("6162616261620000")
                .append("0000000000000000")
                .append("6162616261620000")
                .append("0000000000000000")
                .append("6162616261626100")
                .append("0000000000000000")
                .append("52414e4745524d49")
                .append("4e49000000000000")
                .append("6162616261000000")
                .append("0000000061626162");
        byte[] bytes1 = HexUtils.hexString2bytes(builder2.toString());
        String s2 = new String(bytes1);
        System.out.println("s2 = " + s2);

    }
}
