package com.example.concurrency.extension.t15.conditionP;

/**
 * 消费者
 */
public class Consumer implements Runnable {
    private Buffer buffer;

    Consumer(Buffer b) {
        buffer = b;
    }

    @Override
    public void run() {
        while (true) {
            buffer.take();
        }
    }
}
