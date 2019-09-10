package com.example.concurrency.Chapter2.t20;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装线程安全容器
 * 将非线程安全容器封装成线程安全的容器
 * 或者直接使用SDK包中的 List l = Collections.synchronizedList(new ArrayList<>());
 * @param <T>
 */
public class SafeArrayList<T> {
    List<T> c = new ArrayList<>();

    synchronized T get(int idx) {
        return c.get(idx);
    }

    synchronized void add(int idx, T t) {
        c.add(idx, t);
    }

    synchronized boolean addIfNotExist(T t) {
        if (!c.contains(t)) {
            c.add(t);
            return true;
        }
        return false;
    }
}
