package com.example.concurrency.extension.t05;

/**
 * 账户类
 */
public class Account {
    private Integer banalce;

    public Account(Integer banalce) {
        this.banalce = banalce;
    }

    // 转账方法
    public void transactionToTarget(Account target, Integer money) {
        Allocator.getInstance().apply(this, target);
        if (this.banalce > money) {
            this.banalce -= money;
            target.setBanalce(target.getBanalce() + money);
        }
        Allocator.getInstance().release(this, target);
    }

    public Integer getBanalce() {
        return banalce;
    }

    public void setBanalce(Integer banalce) {
        this.banalce = banalce;
    }
}
