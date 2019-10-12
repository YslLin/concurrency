package com.example.concurrency.Chapter3.t31;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Guarded Suspension 模式 - 等待唤醒机制的实现规范
 */
public class Main {
    AtomicLong atomicLong = new AtomicLong(1001);

    class Message {
        String id;
        String content;

        Message(String id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    public void handleWebReq() {
        long id = atomicLong.getAndIncrement();
        // 创建消息
        Message msg = new Message(id + "", "{...}");
        // 创建 GuardedObject 实例
        GuardedObject<Message> go = GuardedObject.create(id);
        // 发送消息
        // send();
        // 等待 MQ 消息
        Message r = go.get(t -> t != null);
    }

    void onMessage(Message msg) {
        // 唤醒等待的线程
        GuardedObject.fireEvent(msg.id, msg);
    }
}
