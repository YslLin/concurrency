package com.example.concurrency.Chapter2.t17;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁的锁升级降级
 * ReadWriteLock 不支持 锁升级 但允许 锁降级
 */
public class CachedData {
    final Map<String, String> m = new HashMap<>();
    final ReadWriteLock rwl = new ReentrantReadWriteLock();
    final Lock r = rwl.readLock();
    final Lock w = rwl.writeLock();

    String get(String k) {
        String v = null;
        r.lock();
        try {
            v = m.get(k);
            if (v == null) {
                // 释放读锁，因为不允许读锁升级写锁
                r.unlock();
                // 获取写锁
                w.lock();
                try {
                    // 再次检查状态
                    v = m.get(k);
                    if (v == null) {
                        v = "模拟写入缓存数据";
                        m.put(k, v);
                    }
                    // 释放写锁前，降级为读锁
                    r.lock();
                } finally {
                    // 释放写锁
                    w.unlock();
                }
            }
            // 此处仍然持有读锁
        } finally {
            r.unlock();
        }
        return v;
    }

    void put(String k, String v){
        w.lock();
        try {
            m.put(k, v);
        } finally {
            w.unlock();
        }
    }


}
