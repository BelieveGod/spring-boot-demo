package com.example.springbootdemo.firmware.can2;

/**
 * 新的Can 协议信息定义类
 */
public class Can2Protocal {
    public static final short HEADER = 0X550E;

    /* 这里用int 是避免因为用short 导致的溢出错误，因为运算过程还是转化为int 进行运算的 */

    // 获取版本 指令 0X41N
    public static final int GET_VERSION = 0X410;

    // 获取版本 应答 0X4AN
    public static final int FBK_GET_VER = 0X4A0;

    // 开始升级 指令 0X01N
    public static final int START_UPDATE = 0X010;

    // 开始升级 应答 0X0AN
    public static final int FBK_UPDATE = 0X0A0;

    // 写入 指令 范围是0x020 - 0x 02F
    public static final int WRITE_DATA = 0X020;

    // 写入 应答 范围是0x0BN - 0X0BF
    public static final int FBK_WRITE = 0X0b0;

    // 发送版本 指令
    public static final int SEND_VERSION = 0X450;

    // 发送版本 应答
    public static final int FBK_SEND_VERSION = 0X4B0;

    // 版本长度
    public static final int VERSION_SIZE = 80;

    // 写入数据的数据包大小
    public static final int PK_SIZE = 1024;

    // 一帧数据的有效 payload 长度
    public static final int PAYLOAD_SIZE = 8;

    // 节点范围 1-F
    public static final int MIN_ID = 0x1;
    public static final int MAX_ID = 0xf;

    /* ========== 开始升级应答的反馈帧含义 ================ */
    // APP 跳转 BootLoader;
    public static final byte APP_2_BOOT = 0X01;
    // 擦除FLASH
    public static final byte ERASE_FLASH = 0X02;
    // 擦除成功
    public static final byte ERASE_OK = 0x03;
    // 擦除失败
    public static final byte ERASE_FAIL = 0x04;

    /* =================== 写入数据应答的反馈帧含义 =============== */
    // 接收到固件数据
    public static final byte REC_DATA = 0x01;

    // 接收到1K数据，CRC校验成功，开始写入
    public static final byte CRC_OK = 0x02;

    // 接收到1K数据，CRC校验失败，需要重传
    public static final byte CRC_FAIL = 0X03;
    // 写入1K 数据成功
    public static final byte WRITE_OK = 0X04;

    // 写入1K 数据失败
    public static final byte WRITE_FAIL = 0x05;

    // 固件传输成功
    public static final byte FW_OK = 0X06;

    /* ================== 写入版本的应答 ====================*/

    // 写入版本成功
    public static final byte SEND_VER_OK = 0X00;
    // 写入版本成功
    public static final byte SEND_VER_FAIL = 0X01;
    // 类型：写入指令
    public static final byte SD_VER_TP=0x05;


}
