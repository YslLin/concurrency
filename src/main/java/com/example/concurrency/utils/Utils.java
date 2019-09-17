package com.example.concurrency.utils;

import java.util.concurrent.TimeUnit;

public class Utils {

    public static void sleep(long time) {
        sleep(time, TimeUnit.SECONDS);
    }

    static void sleep(long time, TimeUnit unit) {
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
