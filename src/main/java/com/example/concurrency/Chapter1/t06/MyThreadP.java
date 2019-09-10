package com.example.concurrency.Chapter1.t06;

public class MyThreadP implements Runnable {
    private String name;
    private Object prev1;
    private Object self2;
    private Object to3;

    private MyThreadP(String name, Object prev1, Object self2, Object to3) {
        this.name = name;
        this.prev1 = prev1;
        this.self2 = self2;
        this.to3 = to3;
    }

    public void run() {
        int count = 0;
        while (count < 10) {
            synchronized (prev1) {
                synchronized (self2) {
                    synchronized (to3) {
                        count++;
                        System.out.println(name + count);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        self2.notify();
                    }
                }
                try {
                    prev1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();

        MyThreadP pa = new MyThreadP("A", c, a, b);
        MyThreadP pb = new MyThreadP("B", a, b, c);
        MyThreadP pc = new MyThreadP("C", b, c, a);

        new Thread(pa).start();
//        Thread.sleep(10);
        new Thread(pb).start();
//        Thread.sleep(10);
        new Thread(pc).start();
    }
}
