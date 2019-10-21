package com.example.concurrency.Chapter5.t43;

@FunctionalInterface
public interface TxnRunnable {
    void run(Txn txn);
}
