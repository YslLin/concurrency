package com.example.concurrency.Chapter3.t36;

/**
 * 日志
 */
public class LogMsg {
    LEVEL level;
    String msg;

    public LogMsg(LEVEL level, String msg) {
        this.level = level;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "LogMsg{" +
                "level=" + level +
                ", msg='" + msg + '\'' +
                '}';
    }
}
