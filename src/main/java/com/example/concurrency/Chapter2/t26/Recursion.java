package com.example.concurrency.Chapter2.t26;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 传统递归排序与 Fork/Join 比较
 */
public class Recursion {

    public static void main(String[] args) {
        Recursion recursion = new Recursion();
        recursion.exeRecursion();
    }

    void exeRecursion() {
        long startTime = System.currentTimeMillis();
        // 创建随机数组成的数组
        long[] arrs = new long[10000000];
        fillRandom(arrs);
        ForkJoinPool fjp = new ForkJoinPool(4);
        MargeSort ms = new MargeSort(arrs);
        long[] result = mergeSort(arrs);
//        long[] result = fjp.invoke(ms);
        long endTime = System.currentTimeMillis();
//        System.out.println(String.format("耗时：%d; %s", endTime - startTime, Arrays.toString(result)));
        System.out.println(String.format("耗时：%d; %s", endTime - startTime, ""));
    }

    class MargeSort extends RecursiveTask<long[]>{
        long[] arrs;

        MargeSort(long[] arrs) {
            this.arrs = arrs;
        }

        @Override
        protected long[] compute() {
            if(arrs.length < 2) return arrs;
            int mid = arrs.length / 2;
            MargeSort ms1 = new MargeSort(Arrays.copyOfRange(arrs, 0, mid));
            ms1.fork();
            MargeSort ms2 = new MargeSort(Arrays.copyOfRange(arrs, mid, arrs.length));
            return merge(ms2.compute(), ms1.join());
        }

    }

    /**
     * 传统递归
     * 耗时：30508ms
     */
    long[] mergeSort(long[] arrs) {
        if (arrs.length < 2) return arrs;
        int mid = arrs.length / 2;
        long[] left = Arrays.copyOfRange(arrs, 0, mid);
        long[] right = Arrays.copyOfRange(arrs, mid, arrs.length);
        return merge(mergeSort(left), mergeSort(right));
    }

    long[] merge(long[] left, long[] right) {
        long[] result = new long[left.length + right.length];
        for (int i = 0, m = 0, j = 0; m < result.length; m++) {
            if (i >= left.length) {
                result[m] = right[j++];
            } else if (j >= right.length) {
                result[m] = left[i++];
            } else if (left[i] > right[j]) {
                result[m] = right[j++];
            } else result[m] = left[i++];
        }
        return result;
    }

    // 数组填充随机数
    void fillRandom(long[] arr) {
        Random random = new Random();
        for (int i = 0, length = arr.length; i < length; i++) {
            arr[i] = (long) (Math.random() * 10000);
        }
    }
}
