package com.example.concurrency.Chapter5.t43;

/**
 * 带版本号的对象引用
 * 每次修改value都对应着一个唯一的版本号，所以用不变性模式
 * 不可变的
 * @param <T>
 */
public class VersionedRef<T> {
    final T value;
    final long version;

    // 构造方法
    public VersionedRef(T value, long version) {
        this.value = value;
        this.version = version;
    }
}
