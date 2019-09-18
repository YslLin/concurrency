package com.example.concurrency.Chapter2.t26;

import com.example.concurrency.utils.Utils;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * 数组求和
 */
public class Summation {

    public static void main(String[] args) {
        Summation summation = new Summation();
        summation.exeSum();
    }

    void exeSum() {
        // 创建随机数组成的数组
        long[] arr = new long[800];
        fillRandom(arr);
        // 创建分治线程池 最大并发数4
        ForkJoinPool fjp = new ForkJoinPool(4);
        // 创建分治任务
        ForkJoinTask<Long> task = new SumTask(arr, 0, arr.length);
        long startTime = System.currentTimeMillis();
        Long result = fjp.invoke(task);
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("Fork/Join sum: %d in %d ms.", result, endTime - startTime));
    }

    class SumTask extends RecursiveTask<Long> {
        final int THRESHOLD = 100;
        long[] arr;
        int start, end;

        SumTask(long[] arr, int start, int end) {
            this.arr = arr;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            // 如果任务小，直接计算
            if (end - start <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += arr[i];
                }
                Utils.sleep(1);
                System.out.println(String.format("compute %d~%d = %d", start, end, sum));
                return sum;
            }
            // 任务大，一分为二
            int middle = (start + end) / 2;
            System.out.println(String.format("split %d~%d ==> %d~%d, %d~%d", start, end, start, middle, middle, end));
            SumTask st1 = new SumTask(arr, start, middle);
            SumTask st2 = new SumTask(arr, middle, end);
            // 执行compute()方法的线程本身也是一个Worker线程，当对两个子任务调用fork()时，
            // 这个Worker线程就会把任务分配给另外两个Worker，但是它自己却停下来等待不干活了！
//            st1.fork();
//            st2.fork();
//            Long stres1 = st1.join();
//            Long stres2 = st2.join();
            // invokeAll 的N个任务中，其中N-1个任务会使用fork()交给其它线程执行，但是，它还会留一个任务自己执行，这样，就充分利用了线程池，保证没有空闲的不干活的线程。
//            invokeAll(st1, st2);
//            Long stres1 = st1.join();
//            Long stres2 = st2.join();
            // 目测感觉 这种方式最快
            st2.fork();
            Long stres1 = st1.compute();
            Long stres2 = st2.join();

            Long result = stres1 + stres2;
            System.out.println(String.format("result = %d + %d ==> %d", stres1, stres2, result));
            return result;
        }
    }

    // 数组填充随机数
    void fillRandom(long[] arr) {
        Random random = new Random();
        for (int i = 0, length = arr.length; i < length; i++) {
            arr[i] = (long) random.nextInt(100);
        }
    }
}
