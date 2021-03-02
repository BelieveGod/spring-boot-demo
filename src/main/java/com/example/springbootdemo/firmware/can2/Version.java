package com.example.springbootdemo.firmware.can2;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

@Slf4j
public class Version {
    public byte[] hwInfo = new byte[16];    // 硬件版本和批次
    public byte[] motorInfo = new byte[16];    // 电机减速比和批次
    public byte[] nodeType = new byte[16];    // 节点类型
    public byte[] batteryInfo = new byte[12];    // 电池类型含批次
    public byte[] solfVersion = new byte[8];    // 软件版本
    public byte[] productDate = new byte[8];    // 生产日期
    public byte[] canNodeId = new byte[4];    // can节点号

    public Version fromByte(byte[] bytes){
        if(bytes.length!=80){
            log.info("版本长度不等于80");
            throw new IllegalStateException("版本长度不等于80");
        }
        Version version = new Version();
        int pos=0;

        System.arraycopy(bytes, pos, hwInfo, 0, hwInfo.length);
        pos += hwInfo.length;

        System.arraycopy(bytes, pos, motorInfo, 0, motorInfo.length);
        pos += motorInfo.length;

        System.arraycopy(bytes, pos, nodeType, 0, nodeType.length);
        pos += nodeType.length;

        System.arraycopy(bytes, pos, batteryInfo, 0, batteryInfo.length);
        pos += batteryInfo.length;

        System.arraycopy(bytes, pos, solfVersion, 0, solfVersion.length);
        pos += solfVersion.length;

        System.arraycopy(bytes, pos, productDate, 0, productDate.length);
        pos += productDate.length;

        System.arraycopy(bytes, pos, canNodeId, 0, canNodeId.length);
        pos += canNodeId.length;

        return version;
    }

    public byte[] getBytes(){
        byte[] bytes = new byte[80];
        int pos=0;

        System.arraycopy(hwInfo, 0, bytes, pos, hwInfo.length);
        pos += hwInfo.length;

        System.arraycopy(motorInfo, 0, bytes, pos, motorInfo.length);
        pos += motorInfo.length;

        System.arraycopy(nodeType, 0, bytes, pos, nodeType.length);
        pos += nodeType.length;

        System.arraycopy(batteryInfo, 0, bytes, pos, batteryInfo.length);
        pos += batteryInfo.length;

        System.arraycopy(solfVersion, 0, bytes, pos, solfVersion.length);
        pos += solfVersion.length;

        System.arraycopy(productDate, 0, bytes, pos, productDate.length);
        pos += productDate.length;

        System.arraycopy(canNodeId, 0, bytes, pos, canNodeId.length);
        pos += canNodeId.length;

        return bytes;
    }

    public void setHwInfo(String text){
        byte[] bytes = text.getBytes();
        if(bytes.length>16){
            throw new IllegalStateException("要转化hwInfo的字符串字节长度大于16");
        }
        Arrays.fill(hwInfo, (byte) 0);
        System.arraycopy(bytes, 0, hwInfo, 0, bytes.length);
    }

    public void setMotorInfo(String text) {
        byte[] bytes = text.getBytes();
        if(bytes.length>16){
            throw new IllegalStateException("要转化motorInfo的字符串字节长度大于16");
        }
        Arrays.fill(motorInfo, (byte) 0);
        System.arraycopy(bytes, 0, motorInfo, 0, bytes.length);
    }

    public void setNodeType(String text) {
        byte[] bytes = text.getBytes();
        if(bytes.length>16){
            throw new IllegalStateException("要转化nodeType的字符串字节长度大于16");
        }
        Arrays.fill(nodeType, (byte) 0);
        System.arraycopy(bytes, 0, nodeType, 0, bytes.length);
    }

    public void setBatteryInfo(String text) {
        byte[] bytes = text.getBytes();
        if(bytes.length>12){
            throw new IllegalStateException("要转化batteryInfo的字符串字节长度大于12");
        }
        Arrays.fill(batteryInfo, (byte) 0);
        System.arraycopy(bytes, 0, batteryInfo, 0, bytes.length);
    }

    public void setSolfVersion(String text) {
        byte[] bytes = text.getBytes();
        if(bytes.length>8){
            throw new IllegalStateException("要转化solfVersion的字符串字节长度大于8");
        }
        Arrays.fill(solfVersion, (byte) 0);
        System.arraycopy(bytes, 0, solfVersion, 0, bytes.length);
    }

    public void setProductDate(String text) {
        byte[] bytes = text.getBytes();
        if(bytes.length>8){
            throw new IllegalStateException("要转化productDate的字符串字节长度大于8");
        }
        Arrays.fill(productDate, (byte) 0);
        System.arraycopy(bytes, 0, productDate, 0, bytes.length);
    }

    public void setCanNodeId(String text) {
        byte[] bytes = text.getBytes();
        if(bytes.length>canNodeId.length){
            throw new IllegalStateException("要转化canNodeId的字符串字节长度大于4");
        }
        Arrays.fill(canNodeId, (byte) 0);
        System.arraycopy(bytes, 0, canNodeId, 0, bytes.length);
    }
}
