package com.example.concurrency.Chapter1.t05;

/**
 * 解决死锁方法三
 * 破坏循环等待条件
 * 即对资源进行排序，然后按需申请资源。
 * 假设每个资源都有 id 属性，这个 id 可以作为排序字段，申请时我们可以按照从小到大的顺序进行锁定资源。
 * 这样就不会存在 循环 等待了
 */
public class Account {
    private int id;
    private int balance;

    // 转账
    void transfer(Account target, int amt) {
        Account left = this;
        Account right = target;

        if (left.id > target.id) {
            left = target;
            right = this;
        }

        // 锁定序号小的账号
        synchronized (left){
            // 锁定序号大的账号
            synchronized (right) {
                if (this.balance > amt){
                    this.balance -= amt;
                    target.balance += amt;
                }
            }
        }
    }
}
