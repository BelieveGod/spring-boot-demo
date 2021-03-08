package com.example.springbootdemo.firmware.can2.util;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/3/5 10:57
 */
public class CanUtil {

    /**
     * 计算校验和
     * @param bytes 要计算的内容
     * @return
     */
    public static byte getChecksum(byte[] bytes){
        int checkSum=0;
        for(int i=0;i<bytes.length;i++){
            checkSum+=bytes[i];
        }
        return (byte)checkSum;
    }
}
