package com.example.concurrency.t21;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * 原子化对象属性更新器
 * AtomicIntegerFieldUpdater、AtomicLongFieldUpdater、AtomicReferenceFieldUpdater
 * 原子化地更新对象的属性。利用反射机制实现的。
 * <p>
 * 1.属性必须用 volatile 修饰，只有这样才能保证可见性
 * 2.属性不能是包装类型 Integer、Long。 int、long 可以
 * 3.属性不能用 final 修饰
 * 4.通过 newUpdater 静态方法创建实例，参数:操作对象的类，操作属性的名称
 */
public class AtomicFieldUpdater {
//    AtomicIntegerFieldUpdater
//    AtomicLongFieldUpdater
//    AtomicReferenceFieldUpdater


    public static void main(String[] args) {
//        AtomicReferenceFieldUpdater referenceFieldUpdater = AtomicReferenceFieldUpdater.newUpdater()
//        AtomicIntegerFieldUpdater integerFieldUpdater = AtomicIntegerFieldUpdater.newUpdater(Person)
        AtomicFieldUpdater fieldUpdater = new AtomicFieldUpdater();
//        fieldUpdater.exeIntegerFieldUpdater();
        fieldUpdater.exeReferenceFieldUpdater();
    }

    class Person {
        volatile int id;
        volatile String name;

        Person(int id) {
            this.id = id;
        }

        Person(String name) {
            this.name = name;
        }

        void setId(int id) {
            this.id = id;
        }

        int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * AtomicIntegerFieldUpdater
     * int 对象属性更新 使用方式
     */
    void exeIntegerFieldUpdater() {

        Person v = new Person("aa");
        AtomicReference<Person> reference = new AtomicReference<>(v);

        // 参数：对象类，字段名
        AtomicIntegerFieldUpdater<Person> integerFieldUpdater =
                AtomicIntegerFieldUpdater.newUpdater(Person.class, "id");
        Person person = new Person(1000);

        // 加 1 后的值
        integerFieldUpdater.incrementAndGet(person);
        System.out.println("incrementAndGet id: " + person.getId());

        // 加 n 后的值
        integerFieldUpdater.addAndGet(person, 10);
        System.out.println("addAndGet id: " + person.getId());

        // 设置指定值
        integerFieldUpdater.getAndSet(person, 1101);
        System.out.println("getAndSet id: " + person.getId());

    }

    /**
     * AtomicReferenceFieldUpdater
     */
    void exeReferenceFieldUpdater() {
        // 参数:对象类，字段类，字段名
        AtomicReferenceFieldUpdater<Person, String> referenceFieldUpdater =
                AtomicReferenceFieldUpdater.newUpdater(Person.class, String.class, "name");
        Person person = new Person("A");

        // 一个参数的处理函数
        // 参数 当前值 处理后 返回新值
        referenceFieldUpdater.getAndUpdate(person, (old) -> {
            if (old.equals("A")) {
                return "B";
            }
            return "C";
        });
        System.out.println("updateAndGet name:" + referenceFieldUpdater.get(person));

        // 两个参数的处理函数
        // 两个参数 第一个参数是当前值，第二个参数是给定的值 返回新值
        referenceFieldUpdater.getAndAccumulate(person, "B", (old, pre) -> {
            if (old.equals(pre)) {
                return "C";
            }
            return "D";
        });
        System.out.println("getAndAccumulate name:" + referenceFieldUpdater.get(person));

        // CAS 操作逻辑
        String old, newV;
        do {
            old = referenceFieldUpdater.get(person);
            newV = old.equals("C") ? "D" : "E";
        } while (!referenceFieldUpdater.compareAndSet(person, old, newV));
        System.out.println("CAS name:" + referenceFieldUpdater.get(person));

        // 不管原来是啥，现在就改成李四
        referenceFieldUpdater.getAndSet(person,"李四");
        System.out.println("getAndSet name:" + referenceFieldUpdater.get(person));
    }


}
