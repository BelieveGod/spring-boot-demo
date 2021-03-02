package com.example.springbootdemo.firmware.can2;

import java.io.File;

/**
 *
 * 常量类
 * @author LTJ
 * @version 1.0
 * @date 2021/2/19 14:18
 */
public class Constant {
    public static final String CONNECT = "connect";
    public static final String LOG = "log";
    public static final String PROGRESS = "progress";

    // 测试地址
//    private static final String URL_BASE = "http://192.168.0.173:7070";
    // 生产库地址
     private static final String URL_BASE = "http://localhost:7070";

    /**
     * 下载固件
     */
    public static final String URL_FIRMWARE_DOWNLOAD = URL_BASE + "/firmware/download";
    // 查询最新固件
    public static final String GET_LATEST_VERSION_BY_NAME = URL_BASE + "/firmware/getLatestVersionByName";
    // 获取各个固件的最新版本列表
    public static final String GET_ALL_LATEST_VERSION=URL_BASE+"/firmware/getAllLatestVersion";

    public static final String agxHome = System.getProperty("user.home") + File.separator + "agx";

    // 大小端模式
    public static final boolean HIGH=true;
}
