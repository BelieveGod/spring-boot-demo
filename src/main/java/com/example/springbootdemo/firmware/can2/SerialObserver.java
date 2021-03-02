package com.example.springbootdemo.firmware.can2;

import java.util.List;

/**
 * 串口数据观察者接口
 */
public interface SerialObserver {

    void handle(final List<Byte> readBuffer);
}

