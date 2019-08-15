package com.example.concurrency.t21;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 原子化数组
 * AtomicIntegerArray、AtomicLongArray、AtomicReferenceArray
 * 可以原子化地更新数组里面的每一个元素。
 */
public class AtomicArray {

    public static void main(String[] args) {
        AtomicArray array = new AtomicArray();
        AtomicReferenceArray<String> referenceArray = new AtomicReferenceArray<>(5);

        array.exeIntegerArray();
    }

    void exeIntegerArray(){
        /*
          创建一个原子数组，长度为 5
          或 new AtomicIntegerArray(new int[]{1,2,3});
         */
        AtomicIntegerArray integerArray = new AtomicIntegerArray(5);

        // 设置第一个元素为 5
        integerArray.set(0,5);

        int n;
        // 设置第二个元素为 5，返回旧值
        n = integerArray.getAndSet(1, 5);
        System.out.println("getAndSet n:" + n);

        // 第一个元素减 1 ，返回计算后的值
        n = integerArray.decrementAndGet(0);
        System.out.println("decrementAndGet n:" + n);

        // 第一个元素减 1 ，返回计算前的值
        n = integerArray.getAndDecrement(0);
        System.out.println("getAndDecrement n:" + n);

        // 第二个元素加 1 ，返回计算后的值
        n = integerArray.incrementAndGet(1);
        System.out.println("incrementAndGet n:" + n);

        // 第二个元素加 1 ，返回计算前的值
        n = integerArray.getAndIncrement(1);
        System.out.println("getAndIncrement n:" + n);

        // 第三个元素加 5，返回计算前的值
        n = integerArray.getAndAdd(2, 5);
        System.out.println("getAndAdd n:" + n);
        // 第三个元素加 6，返回计算后的值
        n = integerArray.addAndGet(2, 6);
        System.out.println("addAndGet n:" + n);
    }
}
