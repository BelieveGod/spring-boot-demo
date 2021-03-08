package com.example.springbootdemo.firmware.can2.model;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/3/3 18:07
 */
@Slf4j
public class VehicleStatus {

    private VehicleStatus(){
        log.info("VehicleStatus 初始化...");
        for(int i=0;i<4;i++){
            drivers[i] = new Driver();
        }
    }

    private static class SingleHolder{
        private static final VehicleStatus instance = new VehicleStatus();
    }

    public static VehicleStatus getInstance(){
        return SingleHolder.instance;
    }

    public ChassisSys chassisSys=new ChassisSys();
    public Driver[] drivers = new Driver[4];
    public Light light=new Light();
    public Odom odom = new Odom();
    public Motion motion=new Motion();
    public Remoter remoter = new Remoter();
    public BMS bms = new BMS();
}
