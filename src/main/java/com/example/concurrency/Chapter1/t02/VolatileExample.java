package com.example.concurrency.Chapter1.t02;

// 以下代码来源于【参考 1】
class VolatileExample {
    int x = 0;
    volatile boolean v = false;
    public void writer() {
        x = 42;
        v = true;
    }
    public void reader() {
        if (v == true) {
            // 这里 x 会是多少呢？
            System.out.println(x);
        }
    }

    public static void main(String[] args) {
        VolatileExample v = new VolatileExample();
        v.writer();
        v.reader();
    }
}

