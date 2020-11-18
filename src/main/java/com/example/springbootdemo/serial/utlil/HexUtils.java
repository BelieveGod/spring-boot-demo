package com.example.springbootdemo.serial.utlil;

/**
 * @ClassName HexUtils
 * @Description 16进制数据处理的工具类
 * @Author believeGod
 * @Date 2020/11/18 7:31
 * @Version 1.0
 */
public class HexUtils {

    private HexUtils(){
        throw new IllegalStateException("工具类没有实例对象");

    }


    public static String[] bytesToHexString(byte[] bArray){
        String[] strings = new String[bArray.length];
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                strings[i] = "0" + sTemp;
            } else {
                strings[i] = sTemp;
            }
        }
        return strings;
    }


    public static String hexStrings2String(String[] hexStrings){
        StringBuilder builder = new StringBuilder();
        for (String hexString : hexStrings) {
            builder.append(hexString);
        }
        return builder.toString();

    }
}
