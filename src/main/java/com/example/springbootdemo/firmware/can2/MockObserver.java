package com.example.springbootdemo.firmware.can2;

import com.example.springbootdemo.serial.utlil.ByteUtils;
import com.example.springbootdemo.serial.utlil.HexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MockObserver implements SerialObserver{
    private final byte HEADER_1=0X55;
    private final byte HEADER_2=0X0E;
    private AtomicInteger cnt = new AtomicInteger(0);
    private AtomicInteger dropCnt = new AtomicInteger(0);

    // 模拟缓冲区
    public final int bufSize=1024;

    private int readSize=0;

    private int flashSize=0;
    private int flashSizeFix=0;

    private SerialPortService serialPortService;

    public MockObserver(SerialPortService serialPortService) {
        this.serialPortService = serialPortService;
    }

    private List<Byte> receiveData=new LinkedList<>();
    @Override
    public void handle(List<Byte> readBuffer) {
        synchronized (readBuffer){
//            log.info("处理数据：{}",readBuffer.size());
            receiveData.addAll(readBuffer);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readBuffer.clear();
//            log.info("处理后数据：{}，receiveData:{}", readBuffer.size(), receiveData.size());
        }
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
//            byte[] bytes = ArrayUtils.toPrimitive(frame.toArray(new Byte[0]));
//            int i=(bytes[12]& 0xff )<<8 | (bytes[13] &0xff);
            int k = cnt.getAndIncrement();
//            log.info("\n捕获帧{}={},序列号：{}", k,HexUtils.bytesToHexString(frame),i);
//            if(i!=k){
//                log.error("序列号和帧数不等：帧数{}，序列号{},丢失{}帧",k,i,dropCnt.incrementAndGet());
//                cnt.set(i + 1);
//            }
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
        case Can2Protocal.FBK_SEND_VERSION:
        case Can2Protocal.FBK_GET_VER:
        case Can2Protocal.FBK_UPDATE:
        case Can2Protocal.FBK_WRITE:
            log.info("捕获帧：{}", HexUtils.bytesToHexString(frame));
        }

//        switch (cmdType){
//            case Can2Protocal.START_UPDATE:
//                fbkStartUpdate(canFrame);
//                break;
//            case Can2Protocal.GET_VERSION:
//                fbkGetVersion(canFrame);
//                break;
//            case Can2Protocal.WRITE_DATA:
//                fbkWriteData(canFrame);
//                break;
//            case Can2Protocal.SEND_VERSION:
//                fbkSendVersion(canFrame);
//                break;
//        }
    }

    public void fbkStartUpdate(CanFrame canFrame){
        // 节点ID
        int nodeId=canFrame.canId & 0xf;
        log.info("nodeId :{}", nodeId);

        flashSize = (canFrame.data[0]& 0xff) << 24 | (canFrame.data[1]& 0xff) << 16 | (canFrame.data[2]& 0xff) << 8 |(canFrame.data[3]& 0xff);
        log.info("要擦除的flashSize:{}", flashSize);
        int pkSize=0;
        if (flashSize%1024==0){
            pkSize = flashSize / 1024;
        }else{
            pkSize=flashSize/1024+1;
        }
        flashSizeFix = pkSize * 1032;
        log.info("修正的flashSize:{}", flashSizeFix);

        CanFrame fbk = new CanFrame();
        fbk.header = Can2Protocal.HEADER;
        fbk.canId=(short)(Can2Protocal.FBK_UPDATE | nodeId);
        fbk.ack=(byte)0;


        // APP to bootLoaer
        fbk.data[0]=0x01;
        fbk.checksum=getChecksum(canFrame.data);
        byte[] bytes = fbk.getBytes();

        // bootloader中
        fbk.data[0]=0x02;
        fbk.checksum=getChecksum(canFrame.data);
        byte[] bytes2 = fbk.getBytes();
        // flash 擦除成功
        fbk.data[0] = 0x03;
        fbk.checksum=getChecksum(canFrame.data);
        byte[] bytes3 = fbk.getBytes();
        readSize=0;

        try {
            serialPortService.writeData(bytes, 0, bytes.length);
            serialPortService.writeData(bytes2, 0, bytes2.length);
            serialPortService.writeData(bytes3, 0, bytes3.length);
        } catch (IOException e) {
            log.error("发送串口数据错误",e);
        }

    }


    public void fbkWriteData(CanFrame canFrame){
        // 节点ID
        int nodeId=canFrame.canId & 0xf;
        log.info("nodeId :{}", nodeId);

        // 增加缓冲区
        readSize+=8;
        log.info("缓冲区接收到{} 字节", readSize);


        CanFrame fbk = new CanFrame();
        fbk.header = Can2Protocal.HEADER;
        canFrame.canId=(short)(Can2Protocal.FBK_WRITE | nodeId);
        canFrame.ack=(byte)0;

        try {
            if(readSize%1032==0){
//                canFrame.data[0]=0x02;
                canFrame.data[0]=0x03;
                canFrame.checksum = getChecksum(canFrame.data);
                byte[] bytes = canFrame.getBytes();
//                log.info("接受满1K，CRC 成功");
                log.info("接受满1K，CRC 失败");

                serialPortService.writeData(bytes,0,bytes.length);
                if(true){
                    return ;
                }
                if(flashSizeFix==readSize){
                    canFrame.data[0]=0x06;
                    canFrame.checksum = getChecksum(canFrame.data);
                    byte[] bytes2 = canFrame.getBytes();
                    log.info("写入固件 成功");
                    serialPortService.writeData(bytes2,0,bytes2.length);
                }else{
                    canFrame.data[0]=0x04;
                    canFrame.checksum = getChecksum(canFrame.data);
                    byte[] bytes1 = canFrame.getBytes();
                    log.info("写入flash 成功");
                    serialPortService.writeData(bytes1,0,bytes1.length);
                }
            }else{
                canFrame.data[0]=0x01;
                canFrame.checksum = getChecksum(canFrame.data);
                byte[] bytes = canFrame.getBytes();
                log.info("接受到数据成功");
                serialPortService.writeData(bytes,0,bytes.length);
            }
        } catch (IOException e) {
            log.error("回馈写入数据错误", e);
        }

    }


    public void fbkGetVersion(CanFrame canFrame){
        // 节点ID
        int nodeId=canFrame.canId & 0xf;
        log.info("nodeId :{}", nodeId);

        CanFrame fbk = new CanFrame();
        fbk.header=Can2Protocal.HEADER;
        fbk.canId=(short)(Can2Protocal.FBK_GET_VER | nodeId);
        canFrame.ack=(byte)0;

        Version version = new Version();
        version.setHwInfo("V01.002-210120");
        version.setMotorInfo("DEC-90-210120");
        version.setNodeType("SCOUT-V1-MC");
        version.setBatteryInfo("30H-210120");
        version.setSolfVersion("V02.001");
        version.setProductDate("210120");
        version.setCanNodeId("0001");
        byte[] bytes = version.getBytes();

        for(int i=0;i<10;i++){
            System.arraycopy(bytes, i * 8, fbk.data, 0, 8);
            byte[] bytes1 = fbk.getBytes();
            try {
                serialPortService.writeData(bytes1, 0, bytes1.length);
            } catch (IOException e) {
               log.error("发送串口数据错误",e);
               break;
            }
        }
    }


    /**
     * 发送版本
     * @param nodeId
     */
    public void sendVersion(int nodeId){
        CanFrame fbk = new CanFrame();
        fbk.header=Can2Protocal.HEADER;
        fbk.canId=(short)(Can2Protocal.SEND_VERSION | nodeId);
        fbk.ack=(byte)0;

        Version version = new Version();
        version.setHwInfo("V01.002-210120");
        version.setMotorInfo("DEC-90-210120");
        version.setNodeType("D01");
        version.setBatteryInfo("30H-210120");
        version.setSolfVersion("V1.0");
        version.setProductDate("210120");
        version.setCanNodeId("0001");
        byte[] bytes = version.getBytes();
//        byte[] bytes = new byte[80];

        for(int i=0;i<10;i++){
            System.arraycopy(bytes, i * 8, fbk.data, 0, 8);
            byte[] bytes1 = fbk.getBytes();
            try {
                Thread.sleep(700);

                serialPortService.writeData(bytes1, 0, bytes1.length);
            } catch (IOException e) {
                log.error("发送串口数据错误",e);
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setNodeAddr(int oldId,int newId){
        CanFrame canFrame=new CanFrame();
        canFrame.header = Can2Protocal.HEADER;
        canFrame.canId=(short)(0x460 | oldId);
        canFrame.data[0]=(byte)newId;
        canFrame.ack=0;
        canFrame.checksum = getChecksum(canFrame.data);

        byte[] bytes = canFrame.getBytes();
        try {
            serialPortService.writeData(bytes, 0, bytes.length);
        } catch (IOException e) {
            log.error("发送串口数据错误",e);
        }

    }

    public void fbkSendVersion(CanFrame canFrame){
        // 节点ID
        int nodeId=canFrame.canId & 0xf;
        log.info("nodeId :{}", nodeId);

        CanFrame fbk = new CanFrame();
        fbk.header=Can2Protocal.HEADER;
        fbk.canId=(short)(Can2Protocal.FBK_SEND_VERSION | nodeId);
        fbk.ack=(byte)0;

        fbk.data[0] = 0x05;
        fbk.data[1] = 0x00;
        fbk.checksum=getChecksum(fbk.data);
        byte[] bytes = fbk.getBytes();
        try {
            serialPortService.writeData(bytes, 0, bytes.length);
        } catch (IOException e) {
            log.error("发送串口数据错误",e);
        }

    }

    /**
     * 计算校验和
     * @param bytes 要计算的内容
     * @return
     */
    public static byte getChecksum(byte[] bytes){
        int checkSum=0;
        for(int i=0;i<bytes.length;i++){
            checkSum+=bytes[i];
        }
        return (byte)checkSum;
    }

}
