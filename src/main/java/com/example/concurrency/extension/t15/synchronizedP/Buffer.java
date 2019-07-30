package com.example.concurrency.extension.t15.synchronizedP;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Buffer {
    private int maxSize;
    private List<Date> storage;

    Buffer(int size) {
        maxSize = size;
        storage = new LinkedList<>();
    }

    // 生产方法
    public synchronized void put() {
        try {
            // 如果队列满了
            while (storage.size() == maxSize) {
                System.out.println(Thread.currentThread().getName() + ": wait");
                wait(); // 阻塞线程
            }
            storage.add(new Date());
            System.out.println(Thread.currentThread().getName() + ": put:" + storage.size());
            Thread.sleep(1000);
            notifyAll(); // 唤起所有线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 消费方法
    public synchronized void take() {
        try {
            while (storage.size() == 0) { // 如果队列为空
                System.out.println(Thread.currentThread().getName() + ": wait");
                wait(); // 阻塞线程
            }
            Date d = ((LinkedList<Date>) storage).poll();
            System.out.println(Thread.currentThread().getName() + ": take:" + storage.size());
            Thread.sleep(1000);
            notifyAll(); // 唤起所有线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
