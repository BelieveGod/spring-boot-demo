package com.example.springbootdemo.firmware.can2.model;


import com.example.springbootdemo.serial.utlil.ByteUtils;

/**
 * 车辆运动信息
 */
public class Motion {
    public int line_velocity;   //  线速度
    public int angular_velocity;    //  角速度
    public int traverse_velocity;   //  横向速度
    public int steering_angle;   //  转向内转角角度

    // 转换字节数组时候，高位在前
    private boolean high=true;

    public byte[] getBytes(){
        byte[][] bytes = new byte[4][];

        bytes[0] = ByteUtils.short2Bytes((short) line_velocity, high);
        bytes[1] = ByteUtils.short2Bytes((short) angular_velocity, high);
        bytes[2] = ByteUtils.short2Bytes((short) traverse_velocity, high);
        bytes[3] = ByteUtils.short2Bytes((short) steering_angle, high);

        byte[] result = new byte[8];
        for(int i=0;i<bytes.length;i++){
            System.arraycopy(bytes[i], 0, result, i * 2, 2);
        }

        return result;

    }

}
