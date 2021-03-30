package com.example.springbootdemo;

import com.example.springbootdemo.serial.utlil.BitUtil;
import lombok.SneakyThrows;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Demo2 {


    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        Task task = new Task("鸣人");
        Task task2 = new Task("佐助");
//        scheduledExecutorService.scheduleAtFixedRate(task, 0, 100, TimeUnit.MILLISECONDS);
//        scheduledExecutorService.scheduleAtFixedRate(task2, 200, 100, TimeUnit.MILLISECONDS);

        //-52 =11001100,76 =01001100
        int val = -52;
        //结果是1,虽然第0位java中是符号,但是很多通信中不管这个是不是符号, 只管这一位存的是0还是1
//        System.out.println(BitUtil.getBitByInt(val, 0) );
        //结果是0
//        System.out.println(BitUtil.getBitByInt(val, 2) );
        //结果是3
//        System.out.println(BitUtil.getBitByInt(val, 0,1) );
        //结果是76
//        System.out.println(BitUtil.getBitByInt(val, 1,7) );


        int a=0xf;
        for(int i=0;i<8;i++){

            int  result = a & (0x01 << i);
            if(result!=0){
                System.out.println(i+"位置=1");
            }else{
                System.out.println(i+"位置=0");
            }

        }
    }

    public static class Task implements Runnable{
        private String name;

        public Task(String name) {

            this.name=name;
        }

        @SneakyThrows
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+name+"执行任务。。。");
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName()+name+"结束任务。。。");
            Thread.sleep(1000);
        }
    }
}

