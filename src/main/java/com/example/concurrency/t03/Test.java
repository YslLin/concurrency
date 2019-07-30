package com.example.concurrency.t03;

public class Test {
    static long value = 0L;

    synchronized long get() {
        return value;
    }

    synchronized static void addOne() {
        value += 1;
    }


    // 不加 synchronized 80毫秒左右 1000000000 10亿
    // exe 加 synchronized 128毫秒左右 1000000000 10亿
    // addOne 加 synchronized 800毫秒左右 10000000 1千万
    // 总结：不加锁性能最好 循环外加锁性能其次 +1方法加锁性能最低
    // 越频繁加锁解锁性能越低
     void exe() {
        int idx = 0;
        while (idx++ < 10000000) {
            this.addOne();
//            System.out.println(Thread.currentThread().getName() + "----" +this.get());
        }
    }

    public static void main(String[] args) {
        long a= System.currentTimeMillis(); //  获取当前系统时间(毫秒)

        Test test = new Test();
        Thread th1 = new Thread(() -> {
            test.exe();
        });
        Thread th2 = new Thread(() -> {
            test.exe();
        });
        Thread th3 = new Thread(() -> {
            test.exe();
        });
        Thread th4 = new Thread(() -> {
            test.exe();
        });

        th1.start();
        th2.start();
        th3.start();
        th4.start();

        try {
            th1.join();
            th2.join();
            th3.join();
            th4.join();

            System.out.println(test.get());

            System.out.print("程序执行时间为：");
            System.out.println(System.currentTimeMillis()-a+"毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
