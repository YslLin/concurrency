package com.example.concurrency.t18;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

public class TestStampedLock {
    final StampedLock stampedLock = new StampedLock();
    final Map<String, Map<String, Object>> map = new HashMap<>();
    int j = 0;

    public Map<String, Object> get(String key) {
        long stamp = stampedLock.readLock();
        try {
            Thread.sleep(100);
            return map.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stampedLock.unlockRead(stamp);
        }
        return null;
    }

    public Map<String, Object> put(String key, Map<String, Object> v) {
        long stamp = stampedLock.writeLock();
        try {
            Thread.sleep(5000);
            v.put("name", j++);
            map.put(key, v);
            return map.get(key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            stampedLock.unlockWrite(stamp);
        }
        return null;
    }

    public static void main(String[] args) {
        TestStampedLock tsl = new TestStampedLock();
        for (int i = 0; i < 3; i++) {
            new Thread("read-thread-" + i) {
                public void run() {
                    while (true) {
                        System.out.println(Thread.currentThread().getName() + ":" + tsl.get("y"));
                    }
                }
            }.start();
        }

        for (int i = 0; i < 2; i++) {
            new Thread("write-thread-" + i) {
                public void run() {
                    while (true) {
                        System.out.println(Thread.currentThread().getName() + ":" + tsl.put("y", new HashMap<>()) + "\n");
                    }
                }
            }.start();
        }
    }
}
