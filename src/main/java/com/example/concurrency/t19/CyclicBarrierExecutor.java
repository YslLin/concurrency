package com.example.concurrency.t19;

import com.example.concurrency.t19.tool.Reconciliation;

import java.util.Date;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 系统性能优化
 * 查询订单、派送单，然后执行对账，最后将写入差异库。
 * 4.生产消费队列版-查询操作(生产者)和对账操作(消费者)并行化
 * 订单和派送单与对账和保存操作并行化
 * 执行对账操作的同时，预先加载下一轮对账数据查询。
 * 该方案有两个难点：
 * 一个是 订单 和 派送单查询线程的步调要一致，因为要保证 各自生产完一条数据时 对账线程要消费的数据不会乱掉。
 * 另一个是 生产完一轮数据后 能通知到 对账线程 实时消费
 * 可以使用 CyclicBarrier 实现线程同步：
 * 先创建一个计数器初始值为 2 CyclicBarrier
 * 订单查询线程查出一条数据时 调用 barrier.await() 将计数器减1 并等待 计数器为零时继续执行
 * 派送单查询线程查出一条数据时 也调用 barrier.await() 将计数器减1 并等待 计数器为零时继续执行
 * 当两个线程都调用 await() 后，计数器为0，此时两个线程可以执行下一条语句了，同时计数器会执行回调函数
 * 回调函数创建线程 执行对账保存操作，回调函数必须使用线程执行对账操作，因为不使用线程会堵塞主线程，即是说计数器 CyclicBarrier 清零操作是在回调函数执行完之后执行的。
 */
public class CyclicBarrierExecutor {

    public static void main(String[] args) {
        Reconciliation r = new Reconciliation();
        // 创建线程池
        Executor executor1 = Executors.newFixedThreadPool(1);
        Executor executor2 = Executors.newFixedThreadPool(2);

        // 创建队列数据容器
        Vector<Date> s = new Vector<>();
        Vector<Date> pos = new Vector<>();
        Vector<Date> dos = new Vector<>();

        // 第一轮 开始时间
        s.add(new Date());

        // 计数器初始化为2
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, () -> {
            executor1.execute(() -> {
                // 执行对账操作
                long diff = r.check(pos.remove(0), dos.remove(0));
                // 差异写入差异库
                r.save(diff, s.remove(0));
                // 第二轮(第N论) 开始时间
                s.add(new Date());
            });
        });

        // 查询未对账订单
        executor2.execute(() -> {
            while (true) {
                try {
                    pos.add(r.getPOrders());
                    // 计数器减一
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 查询派送单
        executor2.execute(() -> {
            while (true) {
                try {
                    dos.add(r.getDOrders());
                    // 计数器减一
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
