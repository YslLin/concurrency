package com.example.concurrency.Chapter2.t16;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;

/**
 * Semaphore 实现一个限流器
 * 可同时进入多个线程，超出设置上限的线程则阻塞等待
 * @param <T>
 * @param <R>
 */
public class ObjPool<T, R> {
    final List<T> pool;
    final Semaphore sem;

    ObjPool(int size, T t) {
        pool = new Vector<T>() {
        };
        for (int i = 0; i < size; i++) {
            pool.add(t);
        }
        sem = new Semaphore(size);
    }
    
    R exec(Function<T, R> func) {
        T t = null;
        try {
            sem.acquire();
            t = pool.remove(0);
            return func.apply(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            pool.add(t);
            sem.release();
        }
        return null;
    }

    public static void main(String[] args) {
        ObjPool<Long, String> pool = new ObjPool<>(5, 2l);

        for (int i = 0; i < 20; i++) {
            new Thread("thread-" + i) {
                public void run() {
                    pool.exec(t -> {
                        System.out.println(Thread.currentThread().getName() + ":" + t);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return t.toString();
                    });
                }
            }.start();
        }
    }
}
