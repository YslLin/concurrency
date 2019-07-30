package com.example.concurrency.t19.tool;

import java.util.Date;

public class Reconciliation {
    public Date getPOrders() {
        try {
            System.out.println("---getPOrders---");
            Thread.sleep(2000);
            System.out.println("===getPOrders===");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public Date getDOrders() {
        try {
            System.out.println("---getDOrders---");
            Thread.sleep(3000);
            System.out.println("===getDOrders===");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public long check(Date pos, Date dos) {
        try {
            System.out.println("---check---");
            Thread.sleep(500);
            System.out.println("===check===");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return pos.getTime() - dos.getTime();
    }

    public void save(long diff, Date s) {
        try {
            System.out.println("---save---");
            Thread.sleep(1000);
            System.out.println("\t" + (new Date().getTime() - s.getTime()));
            System.out.println("===save===");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
