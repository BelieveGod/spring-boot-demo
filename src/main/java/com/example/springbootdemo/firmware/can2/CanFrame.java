package com.example.springbootdemo.firmware.can2;


import com.example.springbootdemo.serial.utlil.ByteUtils;

import static com.example.springbootdemo.firmware.can2.Constant.*;

/**
 * 新的Can 协议帧定义
 */
public class CanFrame {
    public short header;
    public short canId;
    public byte[] data = new byte[8];
    public byte ack;
    public byte checksum;

    public static final byte sizeOf = (byte)(2+2+8+1+1);

    public byte[] getBytes(){
        byte[] bytes = new byte[sizeOf];
        int length=0;

        byte[] bytes1 = ByteUtils.short2Bytes(header, HIGH);
        System.arraycopy(bytes1,0,bytes,length, bytes1.length);
        length+=bytes1.length;

        byte[] bytes2 = ByteUtils.short2Bytes(canId, HIGH);
        System.arraycopy(bytes2,0,bytes,length, bytes2.length);
        length+=bytes2.length;

        System.arraycopy(data,0,bytes,length,data.length);
        length+=data.length;

        bytes[length++]=ack;
        bytes[length++]=checksum;
        return bytes;
    }

    public static CanFrame from(byte[] bytes){
        CanFrame canFrame=new CanFrame();
        canFrame.header = ByteUtils.bytes2Short(bytes, 0, HIGH);
        canFrame.canId = ByteUtils.bytes2Short(bytes, 2, HIGH);
        System.arraycopy(bytes,4,canFrame.data,0,8);
        canFrame.ack = bytes[12];
        canFrame.checksum = bytes[13];
        return canFrame;
    }
}
