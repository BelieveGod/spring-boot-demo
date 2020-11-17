package com.example.springbootdemo.serial.exception;

/**
 * @ClassName PortInUseException
 * @Description TODO
 * @Author believeGod
 * @Date 2020/11/17 22:59
 * @Version 1.0
 */
public class PortInUseException extends RuntimeException{
    public PortInUseException() {
    }

    public PortInUseException(String message) {
        super(message);
    }

    public PortInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public PortInUseException(Throwable cause) {
        super(cause);
    }

    public PortInUseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
