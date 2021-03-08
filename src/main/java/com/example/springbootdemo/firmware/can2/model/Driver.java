package com.example.springbootdemo.firmware.can2.model;

public class Driver {

    public int rpm=0;     // 电机转速
    public int current=0; // 电机电流
    public int position=0; // 电机当前位置，单位脉冲数
    public int volt=0;        // 驱动器电压
    public int driverTemperature=0;  // 驱动器温度
    public int motorTemerature=0;    // 电机温度
    public int driverInfo=0xff;  // 驱动器状态
}
