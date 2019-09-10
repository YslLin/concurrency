package com.example.concurrency.Chapter2.t21;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/**
 * 原子化的基本数据类型
 * AtomicBoolean、AtomicInteger、AtomicLong
 */
public class AtomicBase {

    public static void main(String[] args) {
        AtomicBase base = new AtomicBase();
//        base.exe();
        base.exeBase();
    }

    /**
     * 原子基本类 AtomicInteger 常用 API
     */
    void exe() {
        AtomicInteger ai = new AtomicInteger(0);
        // i++ 先计算后赋值 ++i 先赋值后计算
        // 即：++ 先计算后赋值
        System.out.println("原子化 i++：" + ai.getAndIncrement());
        System.out.println("原子化 ++i：" + ai.incrementAndGet());
        System.out.println("原子化 i--：" + ai.getAndDecrement());
        System.out.println("原子化 --i：" + ai.decrementAndGet());
        System.out.println("+n 前的值：" + ai.getAndAdd(20));
        System.out.println("+n 后的值：" + ai.addAndGet(-10));

        // CAS 使用的经典范例
        int curValue, newValue;
        do {
            // 获取当前值
            curValue = ai.get();
            // 根据当前值计算新值
            newValue = curValue + 1;
            // CAS 操作，返回是否成功
        } while (!ai.compareAndSet(curValue, newValue));
        System.out.println("使用 CAS 后的值：" + ai.get());

        // 声明一个通用函数 一个参数 返回参数类型的值
        IntUnaryOperator iuo = i -> i < 10 ? ++i : --i;

        // 新值可以通过传入 func 函数来计算
        // 返回计算前的值
        int guAi = ai.getAndUpdate(iuo);
        System.out.println("使用 gu 函数计算：" + guAi);

        // 返回计算后的值
        int ugAi = ai.updateAndGet(iuo);
        System.out.println("使用 ug 函数计算：" + ugAi);

        // 声明一个通用函数 两个相同类型的参数 返回相同类型的值
        IntBinaryOperator ibo = (i, c) -> i + c;

        // 第二个参数表示前面那个参数 3
        int gaAi = ai.getAndAccumulate(3, ibo);
        System.out.println("使用 ga 函数计算：" + gaAi);

        int agAi = ai.accumulateAndGet(4, ibo);
        System.out.println("使用 ag 函数计算：" + agAi);
    }

    /**
     * 原子类 AtomicInteger 并发测试
     */
    void exeBase() {
        AtomicInteger ai = new AtomicInteger(0);
        Integer[] b = {0};
        Thread T1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                ai.getAndIncrement();
                b[0]++;
            }
        });

        Thread T2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                ai.getAndIncrement();
                b[0]++;
            }
        });

        Thread T3 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                ai.getAndIncrement();
                b[0]++;
            }
        });

        Date s = new Date();
        T1.start();
        T2.start();
        T3.start();

        try {
            T1.join();
            T2.join();
            T3.join();
            System.out.println(ai.get());
            System.out.println(b[0]);
            System.out.println("time: " + (new Date().getTime() - s.getTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
