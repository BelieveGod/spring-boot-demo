package com.example.springbootdemo.firmware.can2;

import com.example.springbootdemo.serial.utlil.ByteUtils;
import com.example.springbootdemo.serial.utlil.HexUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
@Slf4j
public class MockAppObserver implements SerialObserver{
    private final byte HEADER_1=0X55;
    private final byte HEADER_2=0X0E;
    private List<Byte> receiveData=new LinkedList<>();
    private SerialPortService serialPortService;


    public MockAppObserver(SerialPortService serialPortService) {
        this.serialPortService = serialPortService;
    }

    @Override
    public void handle(List<Byte> readBuffer) {
        receiveData.addAll(readBuffer);
        readBuffer.clear();
        // 固定长度帧解析
        while(receiveData.size()>=CanFrame.sizeOf){
            // 不是帧头，舍弃一位
            if(receiveData.get(0)!=HEADER_1 || receiveData.get(1)!=HEADER_2){
                receiveData.remove(0);
                continue;
            }
            // 找到帧头，直接获取固定长度
            List<Byte> frame = new ArrayList<>();
            List<Byte> subList = receiveData.subList(0, CanFrame.sizeOf);
            frame.addAll(subList);
            subList.clear();
            log.info("\n捕获帧={}", HexUtils.bytesToHexString(frame));
            // 解析该帧
            decode(frame);
        }
    }


    private void decode(List<Byte> frameBox) {
        byte[] frame = ByteUtils.deBoxed(frameBox);
        CanFrame canFrame = CanFrame.from(frame);

        // 指令类型
        int cmdType=canFrame.canId & 0xff0;
        // 节点ID
        int nodeId=canFrame.canId & 0xf;

        switch (cmdType){
            case Can2Protocal.START_UPDATE:
                break;
            case Can2Protocal.GET_VERSION:
                break;
            case Can2Protocal.WRITE_DATA:
                break;
            case Can2Protocal.SEND_VERSION:
                break;
        }
    }
}
