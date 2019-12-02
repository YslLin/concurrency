package com.example.concurrency.extension.t02;

import com.example.concurrency.utils.Utils;

/**
 * 线程 t1 在 synchronized 中修改变量 i
 * 线程 t2 不使用互斥锁修改变量 i
 * 线程 t3 也在 synchronized 中修改变量 i
 * 线程 t2 不会受到互斥锁的影响，直接修改数据
 * 线程 t3 会受到互斥锁的影响，需等待线程 t1 修改完成之后，再执行
 */
public class Test {
    int i = 0;

    public static void main(String[] args) {
        Test test = new Test();
        Thread t1 = new Thread(()->{
            synchronized (test){
                Utils.sleep(2);
                test.i = 1;
                System.out.println(test.i);
            }
        });
        Thread t2 = new Thread(()->{
            test.i = 2;
            System.out.println(test.i);
        });
        Thread t3 = new Thread(()->{
            synchronized (test){
                test.i = 3;
                System.out.println(test.i);
            }
        });
        t1.start();
        t2.start();
        t3.start();
    }
}
