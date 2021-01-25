package com.example.springbootdemo.firmware;

import cn.hutool.core.io.FileUtil;
import com.example.springbootdemo.serial.Demo.SerialReader;
import com.example.springbootdemo.serial.exception.PortInUseException;
import com.example.springbootdemo.serial.utlil.HexUtils;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/1/25 15:12
 */
@Slf4j
public class MockCan {
    static Map<String, String> response;
    static List<String> inputs=new LinkedList<>();
    static List<String> outputs=new LinkedList<>();;
    public static void main(String[] args) {
        String relativelyPath=System.getProperty("user.dir");
        String fileUrl= relativelyPath + File.separator + "canLogExtract.log";
        final String PORT_NAME = "COM9";
        final int BIT_RATE=460800;
        List<String> data = FileUtil.readLines(fileUrl, "utf-8");
        if(data.size()%3!=0){
            throw new IllegalArgumentException("文件格式错误");
        }
        response = new HashMap<>(data.size() % 3);

        int i=0;
        while(i<data.size()){
            response.put(data.get(i), data.get(i + 1));
            inputs.add(data.get(i));
            outputs.add(data.get(i + 1));
            i=i+3;
        }

        // 开启子线程
        CommPortIdentifier portIdentifier = null;
        System.out.println("加载Mock完毕");

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
            InputStream in = sport.getInputStream();
            // 注册监听者
            sport.addEventListener(new MyListener(in, out));
            // 设置监听开启
            sport.notifyOnDataAvailable(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        // 停留主线程
        Scanner scanner = new Scanner(System.in);
        System.out.println("按任意键结束");
        scanner.nextLine();
        System.exit(0);
    }

    static class MyListener implements SerialPortEventListener{
        final InputStream in;
        final OutputStream out;
        private StringBuilder sb=new StringBuilder();
        private AtomicInteger inCount=new AtomicInteger(0);
        private AtomicInteger outCount=new AtomicInteger(0);

        public MyListener(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
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
                dispose();
                break;
            default:
                break;
            }
        }

        private void dispose() {
            String key = sb.toString();
            sb=new StringBuilder();
            String value = response.get(key);
            if(key.length()==160){
                key=key.substring(80);
                value = "aa55f80f2808000100015555555500000000000000000000000000000000000000000000000000fe";
            }

            if(value!=null){
                if(!value.equals("null")){
                    byte[] bytes = HexUtils.hexString2bytes(value);
                    try {
                        out.write(bytes);
                        System.out.println("回应成功");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                System.out.println("无效key：" + key);
            }
        }

        private void dispose2(){
            String key = sb.toString();
            sb=new StringBuilder();
            if(key.length()==160){
                key=key.substring(80);
                inCount.incrementAndGet();
                outCount.incrementAndGet();
            }
            if (!inputs.get(inCount.get()).equals(key)) {
                System.out.println("输入错误");
                return;
            }
            inCount.incrementAndGet();
            byte[] bytes = HexUtils.hexString2bytes(outputs.get(outCount.getAndIncrement()));
            try {
                out.write(bytes);
                System.out.println("回应成功");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void readComm() {
            try (in){
                int available = in.available();
                byte[] tempBuffer = new byte[in.available()];
                // k用来记录实际读取的字节数
                int k = 0;
                while ((k = in.read(tempBuffer)) != -1) {
                    if (1 > k) {
                        break;
                    }
                    String[] dataHex = HexUtils.bytesToHexStrings(tempBuffer, 0, k);
                    String s = HexUtils.bytesToHexString(tempBuffer, 0, k);
                    sb.append(s);
//                    Byte[] bytes = ArrayUtils.toObject(tempBuffer);
//                    readBuffer.addAll(Arrays.asList(bytes).subList(0, k));
//                    readBuffer.addAll(Arrays.asList(dataHex));
                    // 读到结束符或者没有读入1个字符串就推出循环
//                    System.out.println("\n读取的数据： " + s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
