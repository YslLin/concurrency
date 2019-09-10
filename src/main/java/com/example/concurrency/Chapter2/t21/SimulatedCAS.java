package com.example.concurrency.Chapter2.t21;

/**
 * 原子类的实现原理，基于CAS操作
 * CAS 指令；自旋；
 * CAS 这种方案没有 加锁解锁操作，所以相对于互斥锁方案性能好很多
 * 但 CAS 会有 ABA 问题，即变量值A被其它线程更新成B，之后又被另一个线程更新回A，这样变量值虽然还是A，但其实已经被其它线程更新过了。
 */
public class SimulatedCAS {
    private volatile int count = 0;

    // 模拟实现 CAS (硬件支持，CPU 提供了CAS 指令解决并发问题，全称是 Compare And Swap，即“比较并交换”)
    // 作为一条CPU指令，CAS本身是保证原子性的，所以这里用 synchronized 互斥锁模拟原子性
    private synchronized int cas(int expect, int newValue) {
        // 读目前 count 的值
        int curValue = count;
        // 比较目前 count 值是否 == 期望值
        if (curValue == expect) {
            // 如果是，则更新 count 的值
            count = newValue;
        }
        // 返回写入前的值
        // 这里返回写入前的值是因为，如果更新失败，返回这个已经被其它线程更新的值
        // 方便自旋
        return curValue;
    }

    // 自旋：循环尝试
    // 如果 CAS 返回值与 预期不同 则从新加一重试
    private void addOne() {
        int oldValue,newValue;
        do {
            oldValue = count; // 先拿出来 防止cpu切其他线程修改
            newValue = oldValue + 1;
        } while (oldValue != cas(oldValue, newValue));
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void outCount(){
        try {
            do {
                System.out.println(count);
                Thread.sleep(100);
            } while (true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SimulatedCAS cas = new SimulatedCAS();
        new Thread(()->{
            try {
                do {
                    cas.addOne();
                    Thread.sleep(100);
                } while (true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(cas::outCount).start();
    }
}
