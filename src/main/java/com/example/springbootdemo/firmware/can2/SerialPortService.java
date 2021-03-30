package com.example.springbootdemo.firmware.can2;

import com.example.springbootdemo.serial.utlil.HexUtils;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 封装串口操作的服务类
 * @author LTJ
 * @version 1.0
 * @date 2021/1/15 10:38
 */

@Slf4j
public class SerialPortService {
    // 应用程序唯一的串口对象
    private CommPortIdentifier theCommPortIdentifier;
    private SerialPort theSerialPort;
    private List<Byte> readBuffer = new LinkedList<>();
    private ExecutorService singlePool = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setName("读取线程");
        t.setDaemon(true);
        return  t;
    });

    // 存在多线程增删问题
    private List<SerialObserver> observerList = new ArrayList<>();

    // 串口打开超时毫秒数
    private final int TIME_OUT=3000;
    private final String ownerName = "mock";

    /**
     * 列出所有可用串口
     */
    public static Optional<List<Map<String,String>>> listAllPorts(){

        List<Map<String,String>> serialPorts = new ArrayList();

        // 遍历获取所有串口
        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        while (portIdentifiers.hasMoreElements()) {
            CommPortIdentifier commPortIdentifier = (CommPortIdentifier) portIdentifiers.nextElement();
            if(CommPortIdentifier.PORT_SERIAL == commPortIdentifier.getPortType()){
                Map map=new HashMap();
                map.put("portName",commPortIdentifier.getName());
                serialPorts.add(map);
            }
        }

        return Optional.of(serialPorts);
    }

    /**
     * 根据串口参数打开串口
     *
     */
    public AgxResult openSerialPort(PortParam portParam){
        //  这里应该为了最稳妥的起见，不是null的话就当作打开了串口，逻辑后续再理。
        if(theCommPortIdentifier !=null && theSerialPort!=null){
            theSerialPort.close();
            throw new OpendPortException("串口已经打开");
        }
        try {
            theCommPortIdentifier = CommPortIdentifier.getPortIdentifier(portParam.getPortName());
            theSerialPort = theCommPortIdentifier.open(ownerName, TIME_OUT);
            theSerialPort.setSerialPortParams(portParam.getBauldRate(),portParam.getDataBits(),portParam.getStopBits(),portParam.getParity());

            theSerialPort.addEventListener(new SerialListener());
            theSerialPort.notifyOnDataAvailable(true);

        } catch (NoSuchPortException e) {
            log.error("不存在串口:"+portParam.getPortName(),e);

            return AgxResult.fail("不存在串口",null);
        } catch (PortInUseException e) {
            log.error("端口已占用:"+portParam.getPortName(),e);
            return AgxResult.fail("端口已占用",null);
        } catch (UnsupportedCommOperationException e) {
            log.error("不支持的串口操作:"+portParam.getPortName(),e);
            return AgxResult.fail("不支持的串口操作",null);
        } catch (TooManyListenersException e) {
            log.error("注册了多个监听者",e);
        }
        return AgxResult.ok("打开串口成功",null);
    }


    /**
     * 关闭串口
     *
     */
    public AgxResult closeSeriaPort(){
        if(theSerialPort!=null){
            theSerialPort.close();
        }
        theSerialPort =null;
        theCommPortIdentifier =null;
        observerList.clear();
        return AgxResult.ok("关闭串口成功",null);
    }

    /**
     * 写数据
     */
    public void writeData(byte[] data,int off,int n) throws IOException {
        if(theSerialPort==null){
            log.warn("串口没有打开，不能写入始数据");
            return ;
        }
        OutputStream outputStream = theSerialPort.getOutputStream();
        final String string = HexUtils.bytesToHexString(data);
        log.info("输入指令:{}",string);
        outputStream.write(data,off,n);
        outputStream.flush();
        outputStream.close();
    }

    public void addObserver(SerialObserver observer){
        observerList.add(observer);
    }

    public void removeObserver(SerialObserver observer){
        observerList.remove(observer);
    }

    public void clearObservers(SerialObserver observer){
        observerList.clear();
    }

    public void addEventListener(SerialPortEventListener listener) throws IOException, TooManyListenersException {
        theSerialPort.addEventListener(listener);

    }

    public SerialPort getTheSerialPort() {
        return theSerialPort;
    }

    /* ========================================  内部类串口事件监听者 ====================================== */

    public class SerialListener implements SerialPortEventListener {
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
                    // 读取数据
                    readComm();

                    // 读完数据后通知相应处理
                    singlePool.submit(()->{
//
                        inform();
                    });
                    break;
                default:
                    break;
            }
        }

        /**
         * 读取数据
         */
        private void readComm(){
            try (InputStream in=theSerialPort.getInputStream()){
                int available = in.available();
                byte[] tempBuffer = new byte[in.available()];
                // k用来记录实际读取的字节数
                int k = 0;
                int circulationCnt=0;
                while ((k = in.read(tempBuffer)) != -1) {
//                    log.info("circulationCnt++ = {},readBuffer.size:{}," , circulationCnt++,readBuffer.size());
                    // 读到结束符或者没有读入1个字符串就退出循环
                    if (1 > k) {
                        break;
                    }
                    String[] dataHex = HexUtils.bytesToHexStrings(tempBuffer, 0, k);
                    String s = HexUtils.bytesToHexString(tempBuffer, 0, k);
                    Byte[] bytes = ArrayUtils.toObject(tempBuffer);
                    synchronized (readBuffer){

                        readBuffer.addAll(Arrays.asList(bytes).subList(0, k));
                    }
//                    log.info("\n读取的数据： " + s);
                    if(readBuffer.size()>1400){
                        singlePool.submit(()->{

                            inform();
                        });
                    }
                }
            } catch (IOException e) {
                log.error("串口读IO 错误",e);
            }
        }

        /**
         * 通知数据处理的观察者
         */
        private void inform(){
            for (SerialObserver serialObserver : observerList) {
                // 这里同步调用，应该不会和读数据冲突。避免一边接收数据，一边处理数据
//                log.info("通知处理");
                serialObserver.handle(readBuffer);
            }
        }
    }
}
