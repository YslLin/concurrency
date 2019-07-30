package com.example.concurrency.extension.t15.conditionP;

/**
 * 生产者
 */
public class Producer implements Runnable {
    private Buffer buffer;

    Producer(Buffer b) {
        buffer = b;
    }

    @Override
    public void run() {
        while (true) {
            buffer.put();
        }
    }
}
