package com.example.concurrency.Chapter2.t21;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * 原子化的累加器
 * AtomicLong 是 JDK1.5 开始出现的, LongAdder 是 JDK1.8 开始出现的
 * AtomicLong 使用循环的 CAS 操作去操作 value 的值
 * LongAdder 使用的是将 value 值分离成一个数组,最终的结果，就是这些数组的求和累加。
 * 在并发比较低的时候，LongAdder和AtomicLong的效果非常接近。但是当并发较高时，两者的差距会越来越大。
 * 线程数为1000，每个线程循环数为100000时，LongAdder的效率是AtomicLong的6倍左右。
 * 但是 LongAdder 不可以代替 AtomicLong，LongAdder更多地用于收集统计数据，想要使用cas方法还是要选择AtomicLong。
 */
public class AtomicAdder {
    public static void main(String[] args) {
        AtomicAdder adder = new AtomicAdder();
//        adder.exeLongAdderAPI();
//        adder.exeLongAdder();
//        adder.exeLongAccumulator();
        adder.exeArray();
    }

    void exeLongAdderAPI() {
        // 初始值为 0 且不可设置
        LongAdder longAdder = new LongAdder();

        // 加上 给定的值
        longAdder.add(2);
        // sum 返回当前的总和
        System.out.println("add:" + longAdder.sum());
        // +1
        longAdder.increment();
        // longValue 内部调用的 sum
        System.out.println("increment:" + longAdder.longValue());
        // -1
        longAdder.decrement();
        // 返回 sum() 后转换 float
        System.out.println("decrement:" + longAdder.floatValue());
        // 重置为 0
        longAdder.reset();
        // 返回 sum() 后转换 double
        System.out.println("reset:" + longAdder.doubleValue());
        longAdder.add(5);
        // sum() 后 reset()
        longAdder.sumThenReset();
        // 返回 sum() 后转换 int
        System.out.println("sumThenReset:"+longAdder.intValue());
    }

    void exeLongAdder(){
        LongAdder longAdder = new LongAdder();
        Thread T1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                longAdder.increment();
            }
        });

        Thread T2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                longAdder.increment();
            }
        });

        Thread T3 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                longAdder.increment();
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
            System.out.println(longAdder.sum());
            System.out.println("time: " + (new Date().getTime() - s.getTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * LongAccumulator 是 LongAdder 拓展
     * 可以设置初始值
     * 可以设置累加方式 如 加法、乘法、除法
     * new LongAccumulator((x, y)-> x + y, 2);
     * new LongAccumulator((x, y)-> x * y, 2);
     */
    void exeLongAccumulator(){
        // 函数的第一个参数是当前值 第二个参数是指定值
        LongAccumulator longAccumulator = new LongAccumulator((x, y)-> x * y, 2);
        // get 后转 int
        System.out.println("intValue:"+longAccumulator.intValue());
        // 调用初始化时的函数处理参数
        longAccumulator.accumulate(3);
        System.out.println("accumulate:"+longAccumulator.get());
    }

    /**
     * 插入排序
     * 比冒泡排序更高效
     */
    void exeArray(){
        int[] a = new int[]{3,2,5,4,1,7,9,6,8};
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int val = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > val){
                a[j+1] = a[j];
                j--;
            }
            a[j+1] = val;
        }
        System.out.println(Arrays.toString(a));
    }
}
