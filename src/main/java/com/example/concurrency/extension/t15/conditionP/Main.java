package com.example.concurrency.extension.t15.conditionP;


/**
 * 使用lock/condition实现生产者消费者模式
 * 与 synchronized 的区别：
 * - 当生产者执行put方法时，调用notEmpty.signalAll()只会唤醒notEmpty.await()下的消费者线程。
 * - 当消费者执行塔克方法时，调用notFull.signalAll()只会唤醒notFull.await()下的消费者线程。
 * - 但 synchronized 可以通过使用多个不同对象作为锁，实现多个阻塞队列。
 */
public class Main {
    /**
     * 为什么控制台输出看上去只有一个线程在执行
     * 1.因为公用一个lock锁, 其它线程都是获取lock锁, 然后一个线程因为先获取到lock锁了, 其它线程就阻塞了。
     * 2.因为默认使用非公平锁，先获得锁的线程使用完资源后通知其它线程然后解锁，通知过程较慢，所以该线程重新获取了锁
     * 3.可以使用公平锁解决：等待时间长的线程优先唤醒获得锁
     *  lock = new ReentrantLock(true);
     */
    public static void main(String[] args) {
        Buffer buffer = new Buffer(10);
        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);
        for (int i = 0; i < 3; i++) {
            new Thread(producer, "producer-" + i).start();
        }
        for (int i = 0; i < 3; i++) {
            new Thread(consumer, "consumer-" + i).start();
        }
    }
}
