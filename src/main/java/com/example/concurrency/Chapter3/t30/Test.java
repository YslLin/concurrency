package com.example.concurrency.Chapter3.t30;

public class Test {
    class MyRunnable implements Runnable {
        // 创建一个 ThreadLocal 对象
        private ThreadLocal tl1 = new ThreadLocal();
        // ThreadLocal 泛型，ThreadLocal.withInitial 初始化
        private ThreadLocal<String> tl2 = ThreadLocal.withInitial(() -> "This is the initial value");

        @Override
        public void run() {
            // 储存值
            tl1.set("A thread local value");
            // 读取值
            String v1 = (String) tl1.get();
            // 读取泛型值
            String v2 = tl2.get();
        }
    }

    void exe(){
        MyRunnable mr = new MyRunnable();

        new Thread(mr).start();
        new Thread(mr).start();
    }

    public static void main(String[] args) {
        Test t = new Test();
        t.exe();
    }
}
