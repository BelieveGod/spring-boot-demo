package com.example.springbootdemo.firmware.can2.model;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 底盘系统
 */
@Slf4j
public class ChassisSys {
    public boolean parking=true; //  驻车状态
    public ChassisStasus chassisStasus= ChassisStasus.exception;
    public RemoterMode remoterMode= RemoterMode.can;
    public int voltage=0;
    public int errInfoH=0xff; // 故障信息
    public int errInfoL=0xff; // 故障信息

    public enum ChassisStasus{
        normal,
        emergency,
        exception;

        private static Map<Integer, ChassisStasus> map;
        static{
            map = new HashMap();
            map.put(0x00, normal);
            map.put(0x01,emergency);
            map.put(0x02,exception);
        }
        public static ChassisStasus parse(int key){
            return map.get(key);
        }





    }

    public enum RemoterMode{
        remoter,
        can,
        uart;
        private static Map<Integer, RemoterMode> map;
        static{
            map = new HashMap();
            map.put(0x00, remoter);
            map.put(0x01,can);
            map.put(0x02,uart);
        }
        public static RemoterMode parse(int key){
            return map.get(key);
        }
    }
}
