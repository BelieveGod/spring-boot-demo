package com.example.springbootdemo.firmware.can2;

import com.example.springbootdemo.firmware.can2.model.Light;
import gnu.io.SerialPort;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.springbootdemo.firmware.can2.MockData.*;

@Slf4j
public class MockApp {
    private SerialPortService serialPortService;
    private MockAppObserver observer;
    private String portName ="COM9";
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
    private ChassisTask chassisTask = new ChassisTask();
    private MotionTask motionTask = new MotionTask();
    private DriverHTask driverHTask = new DriverHTask();
    private DriverLTask driverLTask = new DriverLTask();
    private OdomTask odomTask = new OdomTask();
    private RemoterTask remoterTask = new RemoterTask();
    private LightTask lightTask = new LightTask();
    private BmsTask bmsTask = new BmsTask();
    private AtomicInteger cnt = new AtomicInteger(0);
    private LocalTime start;
    private LocalTime end;

    private AtomicInteger chassisCnt = new AtomicInteger();
    private AtomicInteger motionCnt = new AtomicInteger();
    private AtomicInteger driLCnt = new AtomicInteger();
    private AtomicInteger driHCnt = new AtomicInteger();
    private AtomicInteger lightCnt = new AtomicInteger();
    private AtomicInteger bmsCnt = new AtomicInteger();
    private AtomicInteger remoterCnt = new AtomicInteger();
    private AtomicInteger odomCnt = new AtomicInteger();



    public static void main(String[] args) {
        MockApp mockApp = new MockApp();
        mockApp.startMock();

    }

    public MockApp() {
        serialPortService = new SerialPortService();
        observer = new MockAppObserver(serialPortService);
    }

    private PortParam getPortParam(){
        int bauldRate=460800;
        PortParam portParam = new PortParam();
        portParam.setPortName(portName);
        portParam.setBauldRate(bauldRate);
        portParam.setDataBits(PortParam.DATABITS_8);
        portParam.setStopBits(PortParam.STOPBITS_1);
        portParam.setParity(PortParam.PARITY_NONE);
        return portParam;
    }

    /**
     * 开始MOCK
     */
    private void startMock(){
        serialPortService.openSerialPort(getPortParam());
        serialPortService.addObserver(observer);
        scheduler.scheduleAtFixedRate(chassisTask, 0, 100, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(motionTask, 0, 20, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(lightTask, 0, 500, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(remoterTask, 0, 20, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(driverHTask, 0, 20, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(driverLTask, 0, 100, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(odomTask, 0, 20, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(bmsTask, 0, 500, TimeUnit.MILLISECONDS);


    }

    /**
     * 打开串口
     * @param portParam
     * @return
     */
    private boolean openPort(PortParam portParam) {
        if(StringUtils.isBlank(portParam.getPortName())){
            log.error("串口为null，请选择串口");
            return false;
        }
        SerialPort theSerialPort = serialPortService.getTheSerialPort();
        if(theSerialPort!=null){
            serialPortService.closeSeriaPort();
        }
        AgxResult agxResult = serialPortService.openSerialPort(portParam);
        if (agxResult.getCode().equals(200)) {
//            updateMessage("打开串口成功");
            log.debug("打开串口成功\n");
        } else {
            log.error("打开串口失败\n");
            return false;
        }
        return true;
    }

    private class ChassisTask implements Runnable{
        @SneakyThrows
        @Override
        public void run() {
            int i = chassisCnt.getAndIncrement();
            chassSys[12]= ((byte) (i >> 8));
            chassSys[13] = (byte) (i >> 0);
            log.info("发送系统状态：{}",i);
            serialPortService.writeData(chassSys,0,chassSys.length);
        }
    }


    private class MotionTask implements Runnable{
        @SneakyThrows
        @Override
        public void run() {

            int i = motionCnt.getAndIncrement();
            log.info("发送运动状态:{}",i);
            motion[12]= ((byte) (i >> 8));
            motion[13] = (byte) (i >> 0);
            serialPortService.writeData(motion,0,motion.length);
        }
    }


    private class DriverLTask implements Runnable{
        @SneakyThrows
        @Override
        public void run() {
            int i = driLCnt.getAndIncrement();
            log.info("发送驱动器低速：{}",i);
            dri1L[12]= ((byte) (i >> 8));
            dri1L[13] = (byte) (i >> 0);

            i = driLCnt.getAndIncrement();
            log.info("发送驱动器低速：{}",i);
            dri2L[12]= ((byte) (i >> 8));
            dri2L[13] = (byte) (i >> 0);

            i = driLCnt.getAndIncrement();
            log.info("发送驱动器低速：{}",i);
            dri3L[12]= ((byte) (i >> 8));
            dri3L[13] = (byte) (i >> 0);

            i = driLCnt.getAndIncrement();
            log.info("发送驱动器低速：{}",i);
            dri4L[12]= ((byte) (i >> 8));
            dri4L[13] = (byte) (i >> 0);


            serialPortService.writeData(dri1L,0,dri1L.length);
            serialPortService.writeData(dri2L,0,dri2L.length);
            serialPortService.writeData(dri3L,0,dri3L.length);
            serialPortService.writeData(dri4L,0,dri4L.length);
        }
    }

    private class DriverHTask implements Runnable{
        @SneakyThrows
        @Override
        public void run() {
            int i = driHCnt.getAndIncrement();
            log.info("发送驱动器高速：{}",i);
            dri1H[12]= ((byte) (i >> 8));
            dri1H[13] = (byte) (i >> 0);

            i = driHCnt.getAndIncrement();
            log.info("发送驱动器高速：{}",i);
            dri2H[12]= ((byte) (i >> 8));
            dri2H[13] = (byte) (i >> 0);

            i = driHCnt.getAndIncrement();
            log.info("发送驱动器高速：{}",i);
            dri3H[12]= ((byte) (i >> 8));
            dri3H[13] = (byte) (i >> 0);

            i = driHCnt.getAndIncrement();
            log.info("发送驱动器高速：{}",i);
            dri4H[12]= ((byte) (i >> 8));
            dri4H[13] = (byte) (i >> 0);

            serialPortService.writeData(dri1H,0,dri1H.length);
            serialPortService.writeData(dri2H,0,dri2H.length);
            serialPortService.writeData(dri3H,0,dri3H.length);
            serialPortService.writeData(dri4H,0,dri4H.length);
        }
    }

    private class OdomTask implements Runnable{
        @SneakyThrows
        @Override
        public void run() {
            int i = odomCnt.getAndIncrement();
            log.info("发送里程计{}",i);
            odom[12]= ((byte) (i >> 8));
            odom[13] = (byte) (i >> 0);
            serialPortService.writeData(odom,0,odom.length);
        }
    }


    private class RemoterTask implements  Runnable{
        @SneakyThrows
        @Override
        public void run() {
            int i = remoterCnt.getAndIncrement();
            log.info("发送遥控器{}",i);
            remoter[12]= ((byte) (i >> 8));
            remoter[13] = (byte) (i >> 0);
            serialPortService.writeData(remoter,0,remoter.length);
        }
    }

    private class BmsTask implements Runnable{
        @SneakyThrows
        @Override
        public void run() {
            int i = bmsCnt.getAndIncrement();
            log.info("发送BMS{}",i);
            bms[12]= ((byte) (i >> 8));
            bms[13] = (byte) (i >> 0);
            serialPortService.writeData(bms,0,bms.length);
        }
    }

    private class LightTask implements Runnable{
        @SneakyThrows
        @Override
        public void run() {
            int i = lightCnt.getAndIncrement();
//            log.info("发送运动状态:{}",i);
            log.info("发送灯光状态{}",i);
            light[12]= ((byte) (i >> 8));
            light[13] = (byte) (i >> 0);
            serialPortService.writeData(light,0,light.length);
        }
    }

}
