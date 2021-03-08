package com.example.springbootdemo.serial;

import com.example.springbootdemo.serial.exception.PortInUseException;
import com.example.springbootdemo.serial.utlil.HexUtils;
import gnu.io.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Scanner;

/**
 * @author LTJ
 * @version 1.0
 * @date 2020/11/17 17:13
 */
@Slf4j
public class Demo {
    // 类常量
    private static final String PORT_NAME = "COM9";
    private static final int BIT_RATE = 460800;

    public static void main(String[] args) {
        sendInstruction();
        Scanner scanner = new Scanner(System.in);
        System.out.println("按任意键结束");
        scanner.nextLine();
        System.exit(0);
    }

    public static void sendInstruction() {
//        byte[] instruction = new byte[]{0x01, 0x03, 0x00, 0x00, 0x00, 0x02, (byte) 0xc4, 0x0b};
        byte[] instruction = new byte[10];
        Arrays.fill(instruction, (byte) 2);

        CommPortIdentifier portIdentifier = null;
        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(PORT_NAME);
            if (portIdentifier.isCurrentlyOwned()) {
                log.error("error: port is currently in use");
                throw new PortInUseException("端口占用");
            }

            // 打开串口
            SerialPort sport = portIdentifier.open("temperlature", 3000);


            // 设置串口参数
            sport.setSerialPortParams(BIT_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            // 打开输入输出流
            OutputStream out = sport.getOutputStream();
            InputStream in=sport.getInputStream();


            // 注册监听者
            sport.addEventListener(new SerialReader(in));
            // 设置监听开启
            sport.notifyOnDataAvailable(true);

            // 输入指令
            new Thread(new SerialWriter(out,instruction)).start();




        } catch (Exception e) {
            log.error("", e);
        }

    }





    public static class SerialReader implements SerialPortEventListener {
        private final InputStream in;

        public SerialReader(InputStream in) {
            this.in = in;
        }

        @Override
        public void serialEvent(SerialPortEvent ev) {
            switch (ev.getEventType()) {
                case SerialPortEvent.BI:
                case SerialPortEvent.OE:
                case SerialPortEvent.FE:
                case SerialPortEvent.PE:
                case SerialPortEvent.CD:
                case SerialPortEvent.CTS:
                case SerialPortEvent.DSR:
                case SerialPortEvent.RI:
                case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                    break;
                case SerialPortEvent.DATA_AVAILABLE:
                    readComm();
                    break;
                default:
                    break;
            }
        }

        private void readComm() {

        }


    }


    public static class SerialWriter implements Runnable{
        private final OutputStream out;
        private byte[] data;


        public SerialWriter(OutputStream out, byte[] data) {
            this.out = out;
            this.data = data;
        }

        @Override
        public void run() {
            try(out) {
                while(true) {
                    out.write(data);
                    out.flush();
                    // 控制睡眠
                    if(false) {
                        Thread.sleep(1);
                    }
                }
            } catch (IOException | InterruptedException e) {
               log.error("写串口数据出现异常",e);
            }
        }
    }
}
