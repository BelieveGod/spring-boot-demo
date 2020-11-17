package com.example.springbootdemo.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author LTJ
 * @version 1.0
 * @date 2020/11/17 17:13
 */
public class Temperature {
    private static final String COM7="COM7";
    private static final int bitRate=4800;
    public static Optional<Integer> getTemperature(){
        byte[] instruction=new byte[]{0x01,0x03,0x00,0x00,0x00,0x02,(byte)0xc4,0x0b};
        // todo
        CommPortIdentifier portIdentifier = null;
        OutputStream out=null;
        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(COM7);
            if (portIdentifier.isCurrentlyOwned()) {
                System.err.println("error: port is currently in use");
                throw  new RuntimeException("端口占用");
            }
            SerialPort sport = (SerialPort) portIdentifier.open("temperlature", 3000);
            System.out.println("sport.getName() = " + sport.getName());


            // 设置串口参数
            sport.setSerialPortParams(bitRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            // 注册监听者
            sport.addEventListener(new SerialReader(sport));
            // 设置监听开启
            sport.notifyOnDataAvailable(true);

            out = sport.getOutputStream();
            out.write(instruction);
//            out.flush();

            System.out.print("输入指令：");
            String[] hexString = SerialReader.bytesToHexString(instruction);
            for (String s : hexString) {
                System.out.print(" " + s);
            }
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {
        getTemperature();
        Scanner scanner = new Scanner(System.in);
        System.out.println("按任意键结束");
        scanner.nextLine();
        System.exit(0);
    }

    public static class SerialReader implements SerialPortEventListener {

       private  SerialPort sport;

        public SerialReader(SerialPort sport) {
            this.sport = sport;
        }

        @SneakyThrows
        @Override
        public void serialEvent(SerialPortEvent ev) {
            if (SerialPortEvent.DATA_AVAILABLE==ev.getEventType()){
                InputStream in = sport.getInputStream();
//                byte[] readBuffer=new byte[in.available()];
                byte[] readBuffer=new byte[1024];

                int len=0;
                while((len=in.read(readBuffer))!=-1){
                    String[] dataHex = bytesToHexString(readBuffer);
                    System.out.print("接收指令：");
                    for (String s : dataHex) {
                        System.out.print(" " + s);
                    }
                    System.out.println();
                }
            }
        }

        private static final String[] bytesToHexString(byte[] bArray){
            String[] strings = new String[bArray.length];
            String sTemp;
            for (int i = 0; i < bArray.length; i++) {
                sTemp = Integer.toHexString(0xFF & bArray[i]);
                if (sTemp.length() < 2) {
                    strings[i] = "0" + sTemp;
                } else {
                    strings[i] = sTemp;
                }
            }
            return strings;
        }
    }
}
