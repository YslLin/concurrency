package com.example.concurrency.t01;

public class Test {
    private static volatile long count = 0;

    // 使用 synchronized 管程竟然比不使用 synchronized 性能更高！
    // 方法上使用 synchronized 比 不使用 synchronized 性能高 (但通过输出语句可以看到 线程变成串行执行了 会首先执行线程A的一万次 再执行线程B中的一万次)
    // 执行语句 i++ 代码块上加 synchronized 比 不使用 synchronized 性能低
    // 代码块加 synchronized 频繁地强制刷新缓存而影响程序的性能。
    private synchronized void add10K(String a) { // 执行时间是 160 毫秒左右
//    private void add10K(String a) { // 执行时间是 260 毫秒左右
        int idx = 0;
        while (idx++ < 10000000) {
//            synchronized (this){
                count++; // 对易失性字段的非原子操作.易失性字段上的非原子操作是读取字段并更新字段的操作。
//            }
            // 测试 volatile 规则 结果不是很明确
            // 规则是：对一个 volatile 变量的写操作相对于后续对这个 volatile 变量的读操作是可见的
            // 但是 先读出 count 再赋值 count 最终并发结果 并没有是正确的两万
            // volatile字段可以看成是一种不保证原子性的同步但保证可见性的特性
//            long i = count;
//            i++;
//            count = i;

//            System.out.println("A " + count);
//            System.out.println(a + "----" + count + "----" + idx);
//            System.out.println(a + "----" + "----" + idx);
        }
    }

    private  void get10K(){
        int idx = 0;
        while (idx++ < 10000) {
            System.out.println(count);
        }
    }

    public static long calc() throws Exception {
        final Test test = new Test();
        // 创建两个线程，执行 add() 操作
        Thread th1 = new Thread(() -> {
            test.add10K("1111");
        });
        Thread th2 = new Thread(() -> {
            test.add10K("2222");
//            test.get10K();
        });
        // 启动两个线程
        th1.start();
        th2.start();
        // 等待两个线程执行结束
        th1.join();
        th2.join();
        return count;
    }

    public static void main(String[] args) {
        try{
            long a= System.currentTimeMillis(); //  获取当前系统时间(毫秒)
            System.out.println(Test.calc());
            System.out.print("程序执行时间为：");
            System.out.println(System.currentTimeMillis()-a+"毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
