package com.example.springbootdemo.concurrent;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试一个列表一边添加一边删除会不会有问题
 *
 */
@Slf4j
public class ListMulAccessDemo2 {

    private List<Byte> src = new LinkedList();
    private List<Byte> out = new ArrayList<>();
    private List<Byte> data = new ArrayList<>();
    public static void main(String[] args) {
        try {
            ListMulAccessDemo2 listMulAccessDemo = new ListMulAccessDemo2();
            listMulAccessDemo.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ListMulAccessDemo2() {
        byte[] bytes = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04};
        Byte[] bytes1 = ArrayUtils.toObject(bytes);
        data.addAll(Arrays.asList(bytes1));

    }

    private void start(){
        ExecutorService scheduler = Executors.newFixedThreadPool(3);
        AddTask addTask = new AddTask();
        clearTask clearTask = new clearTask();
        scheduler.submit(addTask);
        scheduler.submit(clearTask);

    }

    public class AddTask implements Runnable{
        private AtomicInteger cnt = new AtomicInteger(0);
        @Override
        public void run(){
            try {
                while(true) {
                    src.addAll(data);
                    src.add(1, Byte.valueOf((byte)6));
    //                log.info("{}:t1,src add 后,src 的长度：{},out 的长度{}", cnt.getAndIncrement(),src.size(),out.size());
                    Thread.sleep(20);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class clearTask implements Runnable{
        private AtomicInteger cnt = new AtomicInteger(0);
        @Override
        public void run() {
            try {
                while (true) {
                    for(int i=0;i<src.size();i++){
                        Thread.sleep(100);
                        System.out.println(src.get(i).toString());
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class clearTask2 implements Runnable{
        private AtomicInteger cnt = new AtomicInteger(0);
        @Override
        public void run() {
            try {
                while (true) {
                    for (Byte aByte : src) {
                        Thread.sleep(100);
                        System.out.println("aByte = " + aByte);
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
