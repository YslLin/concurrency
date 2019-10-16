package com.example.concurrency.Chapter3.t36;

import com.example.concurrency.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * 日志组件
 * 异步刷盘方式保存日志
 * 1.数据积累到 500 条需要立即刷盘；
 * 2.ERROR 级别的日志需要立即刷盘；
 * 3.存在未刷盘数据，且 5 秒钟内未曾刷盘，需要立即刷盘。
 */
public class Logger {

    // 任务队列
    final BlockingQueue<LogMsg> bq = new LinkedBlockingQueue<>();
    // flush 批量
    static final int batchSize = 5;
    // 只需要一个线程写日志
    ExecutorService es = Executors.newFixedThreadPool(1);
    // "毒丸" 用于终止日志组件
    final LogMsg poisonPill = new LogMsg(LEVEL.ERROR, "毒丸");

    // 启动写日志线程
    void start() {
        try {
            // 创建临时文件
            // 生成路径 C:\Documents and Settings\Administrator\Local Settings\Temp
            File file = File.createTempFile("foo", ".log");
            final FileWriter writer = new FileWriter(file);
            es.execute(() -> {
                try {
                    // 未刷盘日志数量
                    int curIdx = 0;
                    long preFT = System.currentTimeMillis();
                    while (true) {
                        LogMsg log = bq.poll(5, TimeUnit.SECONDS);
                        System.out.println("任务队列取出任务");
                        // 遇到 “毒丸” 立即停止
                        if(poisonPill.equals(log)) {
                            System.out.println("遇到 “毒丸” 立即停止");
                            break;
                        }
                        // 写日志
                        if (log != null) {
                            System.out.println("写入日志");
                            writer.write(log.toString()+"\\r\\n");
                            curIdx++;
                        }
                        // 如果不存在未刷盘数据，则无需刷盘
                        if (curIdx <= 0) {
                            System.out.println("无日志重试");
                            continue;
                        }
                        // 根据规则刷盘
                        if (log != null && log.level == LEVEL.ERROR
                                || curIdx == batchSize
                                || System.currentTimeMillis() - preFT > 5000) {
                            System.out.println("根据规则刷日志");
                            writer.flush();
                            curIdx = 0;
                            preFT = System.currentTimeMillis();
                        }
                        // 模拟延迟
                        Utils.sleep(1);
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        writer.flush();
                        writer.close();
                        System.out.println("日志组件结束");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 写 INFO 级别日志
    void info(String msg) {
        try {
            bq.put(new LogMsg(LEVEL.INFO, msg));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 写 ERROR 级别日志
    void error(String msg) {
        try {
            bq.put(new LogMsg(LEVEL.ERROR, msg));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 停止日志组件
    void stop(){
        try {
            // 投放毒丸
            bq.put(poisonPill);
            // 停止线程
            es.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Logger logger = new Logger();
        // 启动日志组件
        logger.start();
        // 模拟生产者
        ExecutorService es = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            es.execute(()->{
                logger.info("信息日志：" + finalI);
            });
        }
        es.execute(()->{
            logger.error("错误日志");
        });
        es.shutdown();
        Utils.sleep(2);
        // 投放 毒丸 停止日志组件
        // 当日志组件 消费到 毒丸对象 时停止线程
        logger.stop();
    }
}
