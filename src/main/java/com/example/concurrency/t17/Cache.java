package com.example.concurrency.t17;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Cache<K, V> {
    final Map<K, V> m = new HashMap<>();
    final ReadWriteLock rwl = new ReentrantReadWriteLock();
    final Lock r = rwl.readLock();
    final Lock w = rwl.writeLock();

    V get(K key) {
        r.lock();
        try {
            Thread.sleep(100);
            return m.get(key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            r.unlock();
        }
        return null;
    }

    V put(K key, V v) {
        w.lock();
        try {
            Thread.sleep(6000);
            return m.put(key, v);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            w.unlock();
        }
        return null;
    }

    public static void main(String[] args) {
        Cache<String, Date> cache = new Cache<>();
        for (int i = 0; i < 3; i++) {
            new Thread("read-thread-" + i) {
                public void run() {
                    while (true) {
                        System.out.println(Thread.currentThread().getName() + ":" + cache.get("y"));
                    }
                }
            }.start();
        }

        for (int i = 0; i < 2; i++) {
            new Thread("write-thread-" + i) {
                public void run() {
                    while (true) {
                        System.out.println(Thread.currentThread().getName() + ":" + cache.put("y", new Date()) + "\n");
                    }
                }
            }.start();
        }
    }
}
