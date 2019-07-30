package com.example.concurrency.t20;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 测试同步容器与并发容器的性能
 */
public class Test {
    List<Integer> arrayList = new ArrayList<>();
    List<Integer> collectionsList = Collections.synchronizedList(new ArrayList<>());
    List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        Test test = new Test();
//        test.exeArrayList();
//        test.exeCollections();
//        test.exeIterator();
        test.exeSyncIterator();
    }

    /**
     * ArrayList 多线程插入数据
     * 非线程安全容器
     * 1.数据容易丢失
     * 2.运行容易报数组下标越界异常
     */
    void exeArrayList() {
        // 开始时间
        Date s = new Date();
        CountDownLatch countDownLatch = new CountDownLatch(4);
        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    arrayList.add(j);
                }
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
            System.out.println("list: " + arrayList.size());
            System.out.println("time: " + (new Date().getTime() - s.getTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Collections 多线程插入数据
     * 线程安全-同步容器
     * 1.性能低
     * 2.不会出现下标越界或数据丢失问题
     */
    void exeCollections() {
        // 开始时间
        Date s = new Date();
        CountDownLatch countDownLatch = new CountDownLatch(4);
        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    collectionsList.add(j);
                }
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
            System.out.println("list: " + collectionsList.size());
            System.out.println("time: " + (new Date().getTime() - s.getTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Collections 组合操作 一个线程插入数据 一个线程使用 iterator 迭代器
     * 组合操作需要注意竞态条件问题 添加是原子操作 读取是原子操作 组合到一起并不能保证原子性
     * 插入与迭代同时进行容易报 ConcurrentModificationException 并发修改异常
     */
    void exeIterator() {
        // 开始时间
        Date s = new Date();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(() -> {
            for (int j = 0; j < 10000; j++) {
                collectionsList.add(j);
            }
            countDownLatch.countDown();
        }).start();
        new Thread(() -> {
            Iterator iterator = collectionsList.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
            countDownLatch.countDown();
        }).start();
        try {
            countDownLatch.await();
            System.out.println("list: " + collectionsList.size());
            System.out.println("time: " + (new Date().getTime() - s.getTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Collections 迭代器的正确用法
     * 使用迭代器之前必须保证容器是被锁住的
     */
    void exeSyncIterator() {
        // 开始时间
        Date s = new Date();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(() -> {
            for (int j = 0; j < 10000; j++) {
                collectionsList.add(j);
            }
            countDownLatch.countDown();
        }).start();
        new Thread(() -> {
            // 获取迭代器之前 先锁住容器
            synchronized (collectionsList) {
                Iterator iterator = collectionsList.iterator();
                while (iterator.hasNext()) {
                    System.out.println(iterator.next());
                }
                countDownLatch.countDown();
            }
        }).start();
        try {
            countDownLatch.await();
            System.out.println("list: " + collectionsList.size());
            System.out.println("time: " + (new Date().getTime() - s.getTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * CopyOnWriteArrayList 写入时复制
     * 线程安全-并发容器
     * 1.用于写操作非常少的场景，而且能够容忍读写的短暂不一致。
     * 2.迭代器是只读的，不支持增删改。因为迭代器遍历的仅仅是一个快照。
     */
    void exeCopyOnWriteArrayList() {
        // 开始时间
        Date s = new Date();
        CountDownLatch countDownLatch = new CountDownLatch(4);
        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    collectionsList.add(j);
                }
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
            System.out.println("list: " + collectionsList.size());
            System.out.println("time: " + (new Date().getTime() - s.getTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
