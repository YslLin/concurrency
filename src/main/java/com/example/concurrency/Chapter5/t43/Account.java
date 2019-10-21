package com.example.concurrency.Chapter5.t43;

import com.example.concurrency.utils.Utils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 账户
 */
public class Account {
    // 余额
    private TxnRef<Integer> balance;

    // 构造方法
    public Account(int balance) {
        this.balance = new TxnRef<>(new VersionedRef<>(balance, STMTxn.getTxnId()));
    }

    // 转账操作
    public void transfer(Account target, int amt) {
        STM.atomic((txn) -> {
            Integer from = balance.getValue(txn);
            if (from < amt) {
                System.out.println("余额不足，无法完成转账操作！");
                return;
            }
            balance.setValue(from - amt, txn);
            Integer to = target.balance.getValue(txn);
            target.balance.setValue(to + amt, txn);
        });
    }

    // 赋值
    public void setBalance(int amt) {
        STM.atomic((txn) -> {
            Integer from = balance.getValue(txn);
            balance.setValue(amt, txn);
        });
    }

    // 赋值
    public Integer getBalance() {
        AtomicReference<Integer> from = new AtomicReference<>();
        STM.atomic((txn) -> {
            from.set(balance.getValue(txn));
        });
        return from.get();
    }

}
