package com.example.springbootdemo.firmware.can2;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 对于byte类型数据的处理工具类
 */
public class ByteUtils {

    public static Byte[] boxed(byte[] bytes){
        Byte[] result = new Byte[bytes.length];
        for(int i=0;i<bytes.length;i++){
            result[i] = bytes[i];
        }
        return result;
    }

    public static List<Byte> boxedAsList(byte[] bytes){
        Byte[] bytes1 = ArrayUtils.toObject(bytes);
        List<Byte> list = new LinkedList<>();
        list.addAll(Arrays.asList(bytes1));
        return list;

    }

    public static byte[] deBoxed(Byte[] bytes){
        byte[] result = new byte[bytes.length];
        for(int i=0;i<bytes.length;i++){
            result[i] = bytes[i];
        }
        return result;
    }

    public static String getString(List<Byte> list){
        byte[] bytes = deBoxed(list.toArray(new Byte[0]));
        return new String(bytes);
    }

    public static byte[] deBoxed(List<Byte> list){
        return deBoxed(list.toArray(new Byte[0]));
    }



    public static byte[] short2Bytes(short value,boolean high){
        byte[] bytes=new byte[2];
        if (high) {
            bytes[0]=((byte) (value >> 8));
            bytes[1]=((byte) (value) );
        } else {
            bytes[0]=((byte) (value) );
            bytes[1]=((byte) (value >> 8));
        }
        return bytes;
    }

    /**
     *
     * @param bytes
     * @param startPos 包含startpos
     * @param high 是否大端模式
     * @return
     */
    public static short bytes2Short(byte[] bytes,int startPos,boolean high){
        if(startPos+2>bytes.length){
            throw new IndexOutOfBoundsException("转换short越界");
        }
        int i=0;
        if(high){
            i = (bytes[startPos] & 0xff)<<8;
            i+=(bytes[startPos+1] & 0xff);
        }else{
            i = (bytes[startPos] & 0xff);
            i+=(bytes[startPos+1] & 0xff) << 8;
        }
        return (short)i;
    }

    public static int bytes2Int(byte b1,byte b2){
        return (b1&0xff) << 8 | (b2&0xff);
    }

    public static int bytes2Int(byte b1,byte b2,byte b3,byte b4){
        return (b1&0xff) << 24 | (b2&0xff)<<16 |(b3&0xff)<<8 |(b4&0xff);
    }

    public static short bytes2Short(byte b1,byte b2){
        return (short)((b1&0xff) << 8 | (b2&0xff));
    }
}
