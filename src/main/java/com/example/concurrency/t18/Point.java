package com.example.concurrency.t18;

import java.util.HashMap;
import java.util.concurrent.locks.StampedLock;

/**
 * StampedLock 悲观读锁与乐观读
 * 悲观读锁 与 ReadWriteLock 的 readLock() 读锁相似，支持多个线程同时读取
 * 乐观读 没有锁 其它线程可以获取写锁、读锁
 */
public class Point {
    private int x = 1, y = 1;
    final StampedLock stampedLock = new StampedLock();

    String distanceFromOrigin() {
        long stamp = stampedLock.tryOptimisticRead();
        int curX = x, curY = y;
        if (!stampedLock.validate(stamp)) {
            System.out.println(curX);
            stamp = stampedLock.readLock();
            try {
                Thread.sleep(100);
                curX = x;
                curY = y;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
//        return Math.sqrt(curX * curX + curY + curY);
        return curX+"";
    }

    int setX(){
        long stamp = stampedLock.writeLock();
        try {
            Thread.sleep(5000);
            return this.x = x + 1;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            stampedLock.unlockWrite(stamp);
        }
        return 0;
    }

    int setY(){
        long stamp = stampedLock.writeLock();
        try {
            Thread.sleep(5000);
            return this.y++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            stampedLock.unlockWrite(stamp);
        }
        return 0;
    }

    public static void main(String[] args) {
        Point point = new Point();
        for (int i = 0; i < 3; i++) {
            new Thread("read-thread-" + i) {
                public void run() {
                    while (true) {
                        System.out.println(Thread.currentThread().getName() + ":" + point.distanceFromOrigin());
                    }
                }
            }.start();
        }

        for (int i = 0; i < 2; i++) {
            new Thread("write-thread-" + i) {
                public void run() {
                    while (true) {
                        System.out.println(Thread.currentThread().getName() + ":" + point.setX() + "\n");
//                        System.out.println(Thread.currentThread().getName() + ":" + point.setY() + "\n");
                    }
                }
            }.start();
        }
    }
}
