package com.example.concurrency.Chapter1.t05;

import java.util.ArrayList;
import java.util.List;

/**
 * 解决死锁方法一
 * 破坏占用且等待条件
 * 即一次性申请所有资源
 * 增加一个管理员 Allocator，只允许管理员锁定资源。
 * 来保证只有 A 和 B 两个资源同时存在时才会执行锁定。
 */
public class Allocator {
    private List<Object> als = new ArrayList<>();

    // 一次性申请所有资源
    synchronized boolean apply(Object from, Object to){
        if (als.contains(from) || als.contains(to)){
            return false;
        } else {
            als.add(from);
            als.add(to);
        }
        return true;
    }
    // 一次性归还资源
    synchronized void free(Object from, Object to){
        als.remove(from);
        als.remove(to);
    }

    class Account{
        // actr 应该为单例
        private Allocator actr;
        private int balance;

        // 转账
        void  transfer(Account target, int amt){
            while (!actr.apply(this, target));
            try {
                synchronized (this){
                    synchronized (target){
                        this.balance -= amt;
                        target.balance += amt;
                    }
                }
            } finally {
                actr.free(this, target);
            }
        }
    }
}
