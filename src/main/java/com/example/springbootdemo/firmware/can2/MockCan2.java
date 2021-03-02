package com.example.springbootdemo.firmware.can2;

import gnu.io.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

@Slf4j
public class MockCan2 {
    private SerialPortService serialPortService;
    private MockObserver observer;

    public static void main(String[] args) {
        MockCan2 mockCan2 = new MockCan2();

        mockCan2.startMock();
        // 停留主线程
        Scanner scanner = new Scanner(System.in);
        System.out.println("按任意键结束");
        scanner.nextLine();
        System.exit(0);
    }

    public MockCan2() {
        serialPortService = new SerialPortService();
        observer = new MockObserver(serialPortService);
    }

    private PortParam getPortParam(){
        String value="COM9";
        int bauldRate=460800;
        PortParam portParam = new PortParam();
        portParam.setPortName(value);
        portParam.setBauldRate(bauldRate);
        portParam.setDataBits(PortParam.DATABITS_8);
        portParam.setStopBits(PortParam.STOPBITS_1);
        portParam.setParity(PortParam.PARITY_NONE);
        return portParam;
    }

    private void startMock(){
        serialPortService.openSerialPort(getPortParam());
        serialPortService.addObserver(observer);
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
        serialPortService.addObserver(observer);
        return true;
    }
}
