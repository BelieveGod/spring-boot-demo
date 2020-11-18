package com.example.springbootdemo.serial;

import com.example.springbootdemo.serial.exception.PortInUseException;
import com.example.springbootdemo.serial.utlil.HexUtils;
import gnu.io.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private static final String COM7 = "COM7";
    private static final int BIT_RATE = 4800;

    public static void main(String[] args) {
        listAllPorts();
        Scanner scanner = new Scanner(System.in);
        System.out.println("按任意键结束");
        scanner.nextLine();
        System.exit(0);
    }

    public static void sendInstruction() {
        byte[] instruction = new byte[]{0x01, 0x03, 0x00, 0x00, 0x00, 0x02, (byte) 0xc4, 0x0b};
        CommPortIdentifier portIdentifier = null;
        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(COM7);
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



    public static void listAllPorts() {
        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        log.info("...");
        while (portIdentifiers.hasMoreElements()) {
            CommPortIdentifier commPortIdentifier = (CommPortIdentifier) portIdentifiers.nextElement();
            String portTypeName = getPortTypeName(commPortIdentifier.getPortType());
            System.out.println(commPortIdentifier.getName() + "-" + portTypeName);
        }

    }

    private static String getPortTypeName(int portType) {
        switch (portType) {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL: // 并口
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485: // RS485端口
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL: // 串口
                return "Serial";
            default:
                return "unknown type";
        }
    }

    public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) portList.nextElement();
            switch (com.getPortType()) {
                case CommPortIdentifier.PORT_SERIAL:
                    try {
                        // open:（应用程序名【随意命名】，阻塞时等待的毫秒数）
                        /*
                         * open方法打开通讯端口，获得一个CommPort对象，它使程序独占端口。
                         * 如果端口正被其他应用程序占用，将使用CommPortOwnershipListener事件机制
                         * 传递一个PORT_OWNERSHIP_REQUESTED事件。
                         * 每个端口都关联一个InputStream和一个OutputStream,如果端口是用
                         * open方法打开的，那么任何的getInputStream都将返回相同的数据流对象，除非 有close被调用。
                         */
                        CommPort thePort = com.open(Object.class.getSimpleName(), 50);
                        thePort.close();
                        h.add(com);
                    } catch (PortInUseException e) {
                        // 不可用串口
                        System.out.println("Port, " + com.getName() + ", is in use.");
                    } catch (Exception e) {
                        System.err.println("Failed to open port " + com.getName());
                        e.printStackTrace();
                    }
            }
        }
        return h;
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
            try(in) {
                // 延迟一下等所有数据都输入进来
                Thread.sleep(300);
                byte[] readBuffer=new byte[in.available()];
                while (in.read(readBuffer) != -1) {
                    String[] dataHex = HexUtils.bytesToHexString(readBuffer);
                    log.info("接收指令：{}",HexUtils.hexStrings2String(dataHex));
                    // 读一次成功就跳出循环
                    break;
                }
            } catch (IOException | InterruptedException e) {
               log.info("读串口时出现IO异常",e);
            }
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

                log.info("输入指令:{}",HexUtils.hexStrings2String(HexUtils.bytesToHexString(data)));
                out.write(data);
                out.flush();

            } catch (IOException  e) {
               log.error("写串口数据出现异常",e);
            }
        }
    }
}
