package com.example.springbootdemo.firmware.can2.model;

public class BMS {

    public  int soc;
    public int soh;

    public int voltage;
    public int current;
    public int temperature;

    public byte protect1;   // 电池保护状态1
    public byte protect2;    // 电池保护状态1
    public byte protect3;    // 电池保护状态1
    public byte protect4;    // 电池保护状态1
    public int maxTemp; //  最高温度
    public int minTemp; //  最低温度

}
