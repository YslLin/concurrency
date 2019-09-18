package com.example.concurrency.Chapter2.t26;

import com.example.concurrency.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Fork/Join 分治任务模型
 * 单词计数
 */
public class WordCount {

    public static void main(String[] args) {
        WordCount wordCount = new WordCount();
        wordCount.exeWordCount();
    }

    /**
     * 单词计数
     */
    void exeWordCount() {
        String[] fc = {"hello world", "hello me", "hello fork", "hello join", "fork join in world", "hello QQ", "fork join in world", "fork join in world", "fork join in world", "fork join in world", "fork join in world"};
        // 创建分治线程池
        ForkJoinPool fjp = new ForkJoinPool();
        // 创建分治任务
        MR mr = new MR(fc, 0, fc.length);
        long startTime = System.currentTimeMillis();
        // 启动分治任务
        Map<String, Long> result = fjp.invoke(mr);
        long endTime = System.currentTimeMillis();
        // 输出结果
        result.forEach((k, v) -> System.out.println(k + ": " + v));
        System.out.println((endTime - startTime) + " ms.");
    }

    class MR extends RecursiveTask<Map<String, Long>> {
        private String[] fc;
        private int start, end;

        MR(String[] fc, int start, int end) {
            this.fc = fc;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Map<String, Long> compute() {
            if (end - start == 1)
                return calc(fc[start]);
            else {
                int mid = (start + end) / 2;
                System.out.println(mid);
                MR mr1 = new MR(fc, start, mid);
                MR mr2 = new MR(fc, mid, end);
                // 计算子任务，并返回合并的结果
                // 这种方式最快
                mr1.fork();
                // mr2.compute() 必须放在 mr1.join() 前面，因为 mr1.join() 先执行会阻塞线程，导致 mr2 要等待 mr1 执行完之后才能运行
                return merge(mr2.compute(), mr1.join());
                // 这种方式其次 差不多
//                invokeAll(mr1, mr2);
//                return merge(mr1.join(), mr2.join());
                // 这种方式错误 主线程会闲置 又分了两个线程
//                mr1.fork();
//                mr2.fork();
//                return merge(mr1.join(), mr2.join());
            }
        }

        // 合并结果
        private Map<String, Long> merge(Map<String, Long> r1, Map<String, Long> r2) {
            Map<String, Long> result = new HashMap<>();
            result.putAll(r1);
            r2.forEach((k, v) -> {
                Long c = result.get(k);
                if (c != null)
                    result.put(k, c + v);
                else
                    result.put(k, v);
            });
            return result;
        }

        // 统计单词数量
        private Map<String, Long> calc(String line) {
            Map<String, Long> result = new HashMap<>();
            // 分词
            String[] words = line.split("\\s+");
            for (String w : words) {
                Long v = result.get(w);
                if (v != null)
                    result.put(w, v + 1);
                else
                    result.put(w, 1L);

            }
            Utils.sleep(1);
            return result;
        }
    }
}
