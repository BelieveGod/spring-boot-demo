package com.example.springbootdemo.serial.utlil;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/3/3 19:40
 */
public class BitUtil {

    private BitUtil(){

    }
    /**
     * 把单个字节转换成二进制字符串
     */
    public static String byteToBin(byte b) {
        String zero = "00000000";
        String binStr = Integer.toBinaryString(b & 0xFF);
        if(binStr.length() < 8) {
            binStr = zero.substring(0, 8 -binStr.length()) + binStr;
        }
        return binStr;
    }

    public static String IntToBin(int b){
        return byteToBin(( byte )b);
    }

    /**
     * 获取字节在内存中某一位的值,采用字符取值方式
     */
    public static Integer getBitByByte(byte b, int index) {
        if(index >= 8) { return null; }
        Integer val = null;
        String binStr = byteToBin(b);
        val = Integer.parseInt(String.valueOf(binStr.charAt(index)));
        return val;
    }

    public static Integer getBitByInt(int b,int index){
        return getBitByByte((byte)b, index);
    }

    /**
     * 获取字节在内存中多位的值,采用字符取值方式(包含endIndex位)
     */
    public static Integer getBitByByte(byte b, int begIndex, int endIndex) {
        if(begIndex >= 8 || endIndex >= 8 || begIndex >= endIndex) { return null; }
        Integer val = null;
        String binStr = byteToBin(b);
        val = Integer.parseInt(binStr.substring(begIndex, endIndex +1), 2);
        return val;
    }

    public static Integer getBitByInt(int b,int begIndex, int endIndex){
        return getBitByByte((byte)b, begIndex, endIndex);
    }


}
