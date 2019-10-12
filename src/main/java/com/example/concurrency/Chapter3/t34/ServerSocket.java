package com.example.concurrency.Chapter3.t34;

import com.example.concurrency.utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

/**
 * Worker Thread 模式：避免重复创建线程
 * 优化 t33 的 echo 程序服务端
 */
public class ServerSocket {
    public static void main(String[] args) {
        // 固定线程池
//        ExecutorService es = Executors.newFixedThreadPool(500);
        // 强烈建议使用自定义线程池
        ExecutorService es = new ThreadPoolExecutor(
                50, // 核心线程数
                500, // 最大线程数
                60, // 线程存活时间
                TimeUnit.SECONDS, // 存活时间单位
                // 注意要创建有界队列
                new LinkedBlockingQueue<Runnable>(2000),
                // 建议根据业务需求实现 ThreadFactory
                // 赋予一个业务相关的名字
                r -> new Thread(r, "echo-" + r.hashCode()),
                // 建议根据业务需求实现 RejectedExecutionHandler
                // 即使默认拒绝策略满足需求 也同样建议清晰地指明拒绝策略
                // CallerRunsPolicy 提交任务的线程自己去执行该任务。
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        ServerSocketChannel ssc = null;
        try {
            // ServerSocketChannel.open() 打开一个 ServerSocketChannel
            // .bind() 绑定端口号 8080
            ssc = ServerSocketChannel.open().bind(new InetSocketAddress(8080));
            while (true) {
                // accept() 监听连入的TCP连接。方法会阻塞
                // 一般我们都不会只监听一个连接，所以你可以在一个while-循环内部调用accpet()方法。
                SocketChannel sc = ssc.accept();
                System.out.println("接收请求");
                // 将请求处理任务提交给线程池
                es.execute(() -> {
                    try {
                        // 读 Socket
                        ByteBuffer rb = ByteBuffer.allocateDirect(1024);
                        sc.read(rb);
                        // 模拟处理请求
                        System.out.println("处理请求");
                        Utils.sleep(2);
                        ByteBuffer wb = (ByteBuffer) rb.flip();
                        sc.write(wb);
                        // 关闭 Socket
                        sc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭 ServerSocketChannel
                ssc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            es.shutdown();
        }

    }
}
