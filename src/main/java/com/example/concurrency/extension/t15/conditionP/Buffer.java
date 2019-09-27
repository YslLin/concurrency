package com.example.concurrency.extension.t15.conditionP;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阻塞队列 两个条件变量
 */
public class Buffer {
    private int maxSize;
    private List<Date> storage;
    private Lock lock;
    // 条件变量 队列不满
    private Condition notFull;
    // 条件变量 队列不空
    private Condition notEmpty;
    Buffer(int size){
        maxSize = size;
        storage = new LinkedList<>();
        lock = new ReentrantLock(true);
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    // 生产方法
    public void put() {
        lock.lock();
        try {
            // 如果队列满了
            while (storage.size() == maxSize) {
                System.out.println(Thread.currentThread().getName() + ": wait");
                notFull.await(); // 阻塞生产线程
            }
            storage.add(new Date());
            System.out.println(Thread.currentThread().getName() + ": put:" + storage.size());
            Thread.sleep(1000);
            notEmpty.signalAll(); // 唤起所有消费线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    // 消费方法
    public void take() {
        lock.lock();
        try {
            while (storage.size() == 0) { // 如果队列为空
                System.out.println(Thread.currentThread().getName() + ": wait");
                notEmpty.await(); // 阻塞消费线程
            }
            Date d = ((LinkedList<Date>) storage).poll();
            System.out.println(Thread.currentThread().getName() + ": take:" + storage.size());
            Thread.sleep(1000);
            notFull.signalAll(); // 唤起所有生产线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
