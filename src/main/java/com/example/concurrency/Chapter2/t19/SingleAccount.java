package com.example.concurrency.Chapter2.t19;

import com.example.concurrency.Chapter2.t19.tool.Reconciliation;

import java.util.Date;

/**
 * 系统性能优化
 * 查询订单、派送单，然后执行对账，最后将写入差异库。
 * 1.基础版-单线程串行
 */
public class SingleAccount {

    public static void main(String[] args) {
        Reconciliation r = new Reconciliation();
        // 存在未对账订单
        while (true) {
            // 开始时间
            Date s = new Date();
            // 查询未对账订单
            Date pos = r.getPOrders();
            // 查询派送单
            Date dos = r.getDOrders();
            // 执行对账操作
            long diff = r.check(pos, dos);
            // 差异写入差异库
            r.save(diff,s);
        }
    }
}
