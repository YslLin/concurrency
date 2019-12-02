package com.example.concurrency.Chapter2.t22;

import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Java 线程池
 * 线程池基本原理
 * 设置线程池的大小
 * 1.计算密集型任务：n = cpu + 1；线程池大小等于系统处理器数量 + 1
 * 2.I/O密集型任务：
 * 线程池创建初期不会立即启动，而是等到任务提交时太会启动
 * 核心线程满时不会立即创建新的线程，而是等Queue工作队列满时才会创建新的线程。
 * --所以，如果核心线程数设置为0 且工作队列有一定容量时，只有工作队列满后，才会真正开始执行任务。
 * CallerRuns 调用者运行策略：线程池饱和后，新提交的任务将由调用execute的线程执行，由于主线程执行任务的一段时间内无法提交任务，从而降低了流量，使工作者线程有时间处理正在执行的任务。
 * --这期间主线程不会继续就收请求，因此到达的请求将被保存到TCP层的队列中，如果持续过载，TCP层队列被填满，同样会开始抛弃请求，当服务器过载时，会主键向外蔓延。
 * --从线程池到工作队列到应用程序再到TCP层，最终到达客户端。
 * <p>
 * newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
 * newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
 * newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。
 * newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
 * </p>
 * 为什么要用线程池:
 * 1.减少了创建和销毁线程的次数，每个工作线程都可以被重复利用，可执行多个任务。
 * 2.可以根据系统的承受能力，调整线程池中工作线线程的数目，防止因为消耗过多的内存，而把服务器累趴下(每个线程需要大约1MB内存，线程开的越多，消耗的内存也就越大，最后死机)。
 * Java里面线程池的顶级接口是Executor，但是严格意义上讲Executor并不是一个线程池，而只是一个执行线程的工具。真正的线程池接口是ExecutorService。
 */
public class JavaThreadPool {

    public static void main(String[] args) {
        /*
        ThreadPoolExecutor
        corePoolSize: 核心线程数，即空闲时仍保留的线程数
        maximumPoolSize：最大线程数，即繁忙时最多创建多少个线程数
        keepAliveTime：保留时间，当线程数大于核心线程数，多余的空闲线程等待新任务的最长时间，后终止并回收。
        unit：保留时间的单位，时、分、秒、天、毫秒
        workQueue：任务队列，核心线程占满后，新execute提交的任务会保留到该队列中等待，任务队列填满后，才会创建新的线程且数量不能超过最大线程数，
        new ThreadPoolExecutor(0, Integer.MAX_VALUE,60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
        */
        JavaThreadPool pool = new JavaThreadPool();
//        pool.exeCachedThreadPool();
//        pool.exeFixedThreadPool();
//        pool.exeSingleThreadExecutor();
//        pool.exeScheduledThreadPool();
        pool.exeThreadName();
    }

    class Tasks implements Runnable {
        @Override
        public void run() {
            try {
                int r = (int) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(r);
                System.out.println(Thread.currentThread().getName() + " is over");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 缓存线程池
     * 可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     * 无界线程池, 最多创建 Integer.MAX_VALUE 个线程，运行结果没有重复的线程号
     * 创建线程本身需要很多资源，包括内存，记录线程状态，以及控制阻塞等等。
     * 因此，在需要频繁创建短期异步线程的场景下，newCachedThreadPool能够复用已完成而未关闭的线程来提高程序性能。
     */
    void exeCachedThreadPool() {
        /*
        newCachedThreadPool()
        源码：new ThreadPoolExecutor(0, Integer.MAX_VALUE,60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
        创建一个线程池，该线程池根据需要创建新线程，但在先前构建的线程可用时将重用它们。
        这些池通常会提高执行许多短期异步任务的程序的性能。
        调用execute将重用以前构造的线程（如果可用）。
        如果没有现有线程可用，将创建一个新线程并将其添加到池中。
        未使用60秒的线程将被终止并从缓存中删除。
        因此，一个足够长时间闲置的池将不会消耗任何资源。
        可以使用ThreadPoolExecutor构造函数创建具有类似属性但不同详细信息（例如超时参数）的池。
         */
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 20; i++) {
            exec.execute(new Tasks());
        }
        // shutdown 停止接收新任务，原来的任务(包括队列中的)继续执行，都执行结束后关闭线程池
        // shutdownNow 停止接收新任务，忽略队列中的任务，尝试interrupt中断正在执行的任务，返回未执行的任务列表
        // awaitTermination 阻塞当前线程，等所有已提交的任务执行完，或指定的超时时间到了，返回true(所有线程执行完毕)false(已超时)
        exec.shutdown();
    }

    /**
     * 固定线程池
     * 固定长度线程池，可控制线程最大并发数，超出的线程会在队列中等待。一般在后台执行一些辅助性的任务
     * 固定 3 个线程执行任务
     */
    void exeFixedThreadPool() {
        /*
        newFixedThreadPool()
        源码：new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        创建一个线程池，该线程池用无边界队列,固定数量的线程。
        在任何时候，最多个线程将是活动的处理任务。
        如果在所有线程都处于活动状态时提交了其他任务，则它们将在队列中等待，直到有一个线程可用。
        如果任何线程在关闭之前由于执行过程中的失败而终止，则在需要执行后续任务时，将替换一个新的线程。
        池中的线程将存在，直到它显式ExecutorService Shutdown。
        该线程池因任务队列无界，服务器过载时容易导致OOM资源耗尽，内存用完了的现象。不推荐
         */
        ExecutorService exec = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 20; i++) {
            exec.execute(new Tasks());
        }
        exec.shutdown();
    }

    /**
     * 单线程化线程池
     * 单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
     * 如果这个唯一的线程因为异常结束，那么会有一个新的线程来替代它。
     * 始终一个线程执行任务
     */
    void exeSingleThreadExecutor() {
        /*
        newSingleThreadExecutor()
        new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));
        创建一个执行器，该执行器使用一个工作线程操作一个无界队列。
        (但是请注意，如果这个线程在关闭之前的执行过程中由于失败而终止，那么如果需要执行后续任务，将会有一个新的线程替代它。)
        任务保证按顺序执行，并且在任何给定时间都不会有多个任务处于活动状态。
        与其他等价的{newFixedThreadPool(1)}不同，保证返回的执行器不可重新配置以使用其他线程。
         */
        ExecutorService exec = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 20; i++) {
            exec.execute(new Tasks());
        }
        exec.shutdown();
    }

    /**
     * 定时线程池
     * 创建一个线程池，该线程池可以安排命令在给定延迟后运行，或者定期执行。
     */
    void exeScheduledThreadPool() {
        // 定长线程池，支持定时及周期性任务执行
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(10);
        AtomicReference<Date> oldDate = new AtomicReference<>();
        oldDate.set(new Date());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    System.out.println(Thread.currentThread().getName() + " is over " + (new Date().getTime() - oldDate.get().getTime()));
                    oldDate.set(new Date());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 2; i++) {
            // 延迟3s后运行
//            exec.schedule(r, 3, TimeUnit.SECONDS);
            // 首次执行延迟 1s, 后每隔 3s 重复执行
            // 任务执行时间 5s，该定时周期则实质ForkJoinPool 为 5s
            // 当执行任务时间大于间隔时间，此方法不会重新开启一个新的任务进行执行，而是等待原有任务执行完成，马上开启下一个任务进行执行。此时，执行间隔时间已经被打乱。
//            exec.scheduleAtFixedRate(r, 1, 3, TimeUnit.SECONDS);
            // 每次执行结束，已固定时延迟下次执行
            // 该定时周期实质为 8s
            // 当执行任务小于延迟时间时，第一个任务执行之后，延迟指定时间，然后开始执行第二个任务。
            exec.scheduleWithFixedDelay(r, 1, 3, TimeUnit.SECONDS);
        }
        // 周期性任务不能使用 shutdown，因为会直接停止，周期任务不会执行
//        exec.shutdown();
    }

    /**
     * 给线程指定名称
     */
    void exeThreadName(){
//        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
//        threadPoolTaskExecutor.setThreadNamePrefix("fangfa1");
//        ExecutorService exec = Executors.newCachedThreadPool();
//        exec.execute(()->{
//            System.out.println(Thread.currentThread().getName());
//        });
//        exec.shutdown();

        ExecutorService exec = new ThreadPoolExecutor(5,5,1,TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(), new NameThreadFactory("DIY"));
        for (int i = 0; i < 20; i++) {
            exec.execute(new Tasks());
        }
        exec.shutdown();
    }
}
