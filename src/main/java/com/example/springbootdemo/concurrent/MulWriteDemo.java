package com.example.springbootdemo.concurrent;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多个写线程写数据看是否会发生并发错误
 */
@Slf4j
public class MulWriteDemo {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        MulWriteDemo mulWriteDemo = new MulWriteDemo();
        mulWriteDemo.start();
    }


    private void start() throws FileNotFoundException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        String s = System.getProperty("user.dir") + File.separator + "destination.txt";
        log.info("file path:{}", s);
        File file = new File(s);
//        InputStream resourceAsStream = MulWriteDemo.class.getResourceAsStream("/sources.txt");
//        byte[] bytes = IoUtil.readBytes(resourceAsStream);
        byte[] bytes1 = new byte[1 << 15];
        byte[] bytes2 = new byte[1 << 15];
        Arrays.fill(bytes1,(byte)67);
        Arrays.fill(bytes2,(byte)66);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        WriteThread writeThread = new WriteThread(fileOutputStream,bytes1);
        WriteThread writeThread2 = new WriteThread(fileOutputStream,bytes2);
        executorService.submit(writeThread);
        executorService.submit(writeThread2);

        Thread.sleep(1000);
        countDownLatch.countDown();
    }

    private class WriteThread implements Runnable{
        private OutputStream outputStream;
        private byte[] data;

        public WriteThread(OutputStream outputStream,byte[] data) {
            this.outputStream = outputStream;
            this.data = data;
        }

        @Override
        public void run() {
            try {
                log.info("{}：等待开始", Thread.currentThread().getName());

                countDownLatch.await();
                log.info("{}:开始写入", Thread.currentThread().getName());
                outputStream.write(data);
                log.info("{}:写入结束", Thread.currentThread().getName());

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }


}
