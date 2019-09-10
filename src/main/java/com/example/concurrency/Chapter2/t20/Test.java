package com.example.concurrency.Chapter2.t20;

import java.util.*;
import java.util.concurrent.*;

/**
 * 测试同步容器与并发容器的性能
 */
public class Test {
    List<Integer> arrayList = new ArrayList<>();
    List<Integer> collectionsList = Collections.synchronizedList(new ArrayList<>());
    List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
    Map<String,Integer> concurrentHashMap = new ConcurrentHashMap<>();
    Map<String, Integer> concurrentSkipListMap = new ConcurrentSkipListMap<>();

    public static void main(String[] args) {
        Test test = new Test();
//        test.exeArrayList();
//        test.exeCollections();
//        test.exeIterator();
//        test.exeSyncIterator();
//        test.exeCopyOnWriteArrayList();
//        test.exeCopyOnWriteArrayList1();
//        test.exeConcurrentHashMap();
//        test.exeConcurrentSkipListMap();
        test.exeQueue();
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
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(() -> {
            for (int j = 0; j < 10000; j++) {
                copyOnWriteArrayList.add(j);
            }
            countDownLatch.countDown();
        }).start();
        new Thread(() -> {
            // 获取迭代器之前 先锁住容器
            synchronized (collectionsList) {
                Iterator iterator = copyOnWriteArrayList.iterator();
                while (iterator.hasNext()) {
                    System.out.println(iterator.next());
                }
                countDownLatch.countDown();
            }
        }).start();
        try {
            countDownLatch.await();
            System.out.println("list: " + copyOnWriteArrayList.size());
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
    void exeCopyOnWriteArrayList1() {
        // 开始时间
        Date s = new Date();
        int max = 10000;
        ExecutorService service = Executors.newFixedThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(max * 2);
        for (int i = 0; i < max; i++) {
            int finalI = i;
            service.execute(new Thread(() -> {
                arrayList.add(finalI);
                countDownLatch.countDown();
            }));
            service.execute(new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + ":" + arrayList.get(arrayList.size() - 1) + ":" + finalI);
                countDownLatch.countDown();
            }));
        }
        try {
            countDownLatch.await();
            System.out.println("list: " + copyOnWriteArrayList.size());
            System.out.println("time: " + (new Date().getTime() - s.getTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * ConcurrentHashMap 并发容器 分段锁Map
     * 1. 分段锁，key 与 value 不能为空 会报错空指针异常
     * 多线程环境下，使用Hashmap进行put操作会引起死循环，导致CPU利用率接近100%
     * HashTable之所以效率低下主要是因为其实现使用了synchronized关键字对put等操作进行加锁，而synchronized关键字加锁是对整个对象进行加锁，也就是说在进行put等修改Hash表的操作时，锁住了整个Hash表，从而使得其表现的效率低下；
     * ConcurrentHashMap在对象中保存了一个Segment数组，即将整个Hash表划分为多个分段；而每个Segment元素，即每个分段则类似于一个Hashtable；这样，在执行put操作时首先根据hash算法定位到元素属于哪个Segment，然后对该Segment加锁即可。
     */
    void exeConcurrentHashMap(){
        // 开始时间
        Date s = new Date();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(() -> {
            for (int j = 0; j < 10000; j++) {
                concurrentHashMap.put(j+"",j);
            }
            countDownLatch.countDown();
        }).start();
        new Thread(() -> {
            Iterator iterator = concurrentHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                System.out.println(((Map.Entry)iterator.next()).getValue());
            }
            countDownLatch.countDown();
        }).start();
        try {
            countDownLatch.await();
            System.out.println("list: " + concurrentHashMap.size());
            System.out.println("time: " + (new Date().getTime() - s.getTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * ConcurrentSkipListMap 并发容器 跳表Map
     * 1. 查找效率高，key 与 value 不能为空 会报错空指针异常
     * 2. key 是有序的，底层是通过跳表来实现的
     * 3. 跳表是一个链表，但是通过使用“跳跃式”查找的方式使得插入、读取数据时复杂度变成了O（logn）。
     * 4. “先大步查找确定范围，再逐渐缩小迫近”
     * 5. 利用底层的插入、删除的CAS原子性操作，通过死循环不断获取最新的结点指针来保证不会出现竞态条件。
     */
    void exeConcurrentSkipListMap(){
        // 开始时间
        Date s = new Date();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(() -> {
            for (int j = 0; j < 10000; j++) {
                concurrentSkipListMap.put(j+"",j);
            }
            countDownLatch.countDown();
        }).start();


        try {
            Thread.sleep(100);
            new Thread(() -> {
                Iterator iterator = concurrentSkipListMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    System.out.println(((Map.Entry)iterator.next()).getValue());
                }
                countDownLatch.countDown();
            }).start();
            countDownLatch.await();
            System.out.println("list: " + concurrentSkipListMap.size());
            System.out.println("time: " + (new Date().getTime() - s.getTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void exeQueue(){
        Queue<String> queue = new LinkedList<String>();
        queue.offer("1");
        queue.offer("2");
        queue.offer("3");
        queue.offer("4");

        for (String q:queue){
            System.out.println(q);
        }
        System.out.println("---------移除并返问队列头部的元素");
        System.out.println("poll:"+queue.poll());

        for (String q:queue){
            System.out.println(q);
        }
        System.out.println("---------返回队列头部的元素;如果队列为空，则抛出一个NoSuchElementException异常 ");
        System.out.println("element:"+queue.element());

        for (String q:queue){
            System.out.println(q);
        }

        System.out.println("---------返回队列头部的元素;如果队列为空，则返回null");
        System.out.println("peek:"+queue.peek());

        for (String q:queue){
            System.out.println(q);
        }
    }
}
