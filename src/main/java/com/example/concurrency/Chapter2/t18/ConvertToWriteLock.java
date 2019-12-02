package com.example.concurrency.Chapter2.t18;

import com.example.concurrency.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.StampedLock;

/**
 * StampedLock 读锁升级写锁
 */
public class ConvertToWriteLock {

    private double x, y;
    private final StampedLock sl = new StampedLock();

    /**
     * 移动原点
     */
    void moveIfAtOrigin(double newX, double newY) {
        long stamp = sl.readLock();
        Collections. synchronizedList(new ArrayList());
        try {
            while (x == 0.0 && y == 0.0) {
                // 升级为写锁
                long ws = sl.tryConvertToWriteLock(stamp);
                if (ws != 0L) {
                    stamp = ws;
                    x = newX;
                    y = newY;
                    break;
                } else {
                    sl.unlockRead(stamp);
                    stamp = sl.writeLock();
                }
            }
        } catch (Exception e)  {
            e.printStackTrace();
        } finally {
            sl.unlock(stamp);
        }
    }

    void update(double x, double y){
        long stamp = sl.writeLock();
        System.out.println(stamp+":22");
        long ws = sl.tryConvertToWriteLock(stamp);
        System.out.println(ws+":22");
        try {
            System.out.println("w");
            Utils.sleep(3);
            this.x = x;
            this.y = y;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    @Override
    public String toString() {
        return String.format("X: %f, Y: %f", x, y);
    }

    public static void main(String[] args) {
        ConvertToWriteLock c = new ConvertToWriteLock();

        new Thread(()->{
            c.moveIfAtOrigin(1.1, 2.2);
            System.out.println(c);
        }).start();

        new Thread(()->{
            c.update(4.4, 5.5);
            System.out.println(c);
        }).start();

    }
}