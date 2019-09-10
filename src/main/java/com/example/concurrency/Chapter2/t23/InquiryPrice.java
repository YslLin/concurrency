package com.example.concurrency.Chapter2.t23;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * 询价应用
 * 从三个电商询价，然后保存到数据库里。
 */
public class InquiryPrice {

    public static void main(String[] args) {
        InquiryPrice inquiryPrice = new InquiryPrice();
//        inquiryPrice.exeScheme1();
        inquiryPrice.exeScheme2();
    }

    void exeScheme1(){
        Date start = new Date();
        InquiryPrice inquiryPrice = new InquiryPrice();
        ExecutorService exe = Executors.newFixedThreadPool(6);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        Future<String> r1 = exe.submit(() -> {
            return inquiryPrice.getPriceByS1();
        });
        Future<String> r2 = exe.submit(() -> {
            return inquiryPrice.getPriceByS2();
        });
        Future<String> r3 = exe.submit(() -> {
            return inquiryPrice.getPriceByS3();
        });
        exe.execute(() -> {
            try {
                inquiryPrice.save(r1.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        });
        exe.execute(() -> {
            try {
                inquiryPrice.save(r2.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        });
        exe.execute(() -> {
            try {
                inquiryPrice.save(r3.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
            System.out.println("询价结束：" + (new Date().getTime() - start.getTime()));
            exe.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getPriceByS1() {
        try {
            TimeUnit.SECONDS.sleep(1);
            return "PriceByS1: 1";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "PriceByS1: 失败";
    }

    public String getPriceByS2() {
        try {
            TimeUnit.SECONDS.sleep(1);
            return "PriceByS2: 1";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "PriceByS2: 失败";
    }

    public String getPriceByS3() {
        try {
            TimeUnit.SECONDS.sleep(1);
            return "PriceByS3: 1";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "PriceByS3: 失败";
    }

    public void save(String price) {
        try {
            if (price.contains("PriceByS1")) {
                System.out.println("Save PriceByS1");
            } else if (price.contains("PriceByS2")) {
                System.out.println("Save PriceByS2");
            } else if (price.contains("PriceByS3")) {
                System.out.println("Save PriceByS3");
            }
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final ExecutorService executor;

    static {
        executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000));
    }

    static class S1Task implements Callable<String> {
        @Override
        public String call() throws Exception {
            return getPriceByS11();
        }
    }

    static class S2Task implements Callable<String> {
        @Override
        public String call() throws Exception {
            return getPriceByS21();
        }
    }

    static class S3Task implements Callable<String> {
        @Override
        public String call() throws Exception {
            return getPriceByS31();
        }
    }

    static class SaveTask implements Callable<Boolean> {
        private List<FutureTask<String>> futureTasks;

        public SaveTask(List<FutureTask<String>> futureTasks) {
            this.futureTasks = futureTasks;
        }

        @Override
        public Boolean call() throws Exception {
            for (FutureTask<String> futureTask : futureTasks) {
                String data = futureTask.get(10, TimeUnit.SECONDS);
                saveData(data);
            }
            return Boolean.TRUE;
        }
    }

    private static String getPriceByS11() {
        try {
            TimeUnit.SECONDS.sleep(1);
            return "PriceByS1: 1";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "PriceByS1: 失败";
    }

    private static String getPriceByS21() {
        try {
            TimeUnit.SECONDS.sleep(1);
            return "PriceByS2: 1";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "PriceByS2: 失败";
    }

    private static String getPriceByS31() {
        try {
            TimeUnit.SECONDS.sleep(1);
            return "PriceByS3: 1";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "PriceByS3: 失败";
    }

    private static void saveData(String data) {
        try {
            if (data.contains("PriceByS1")) {
                System.out.println("Save PriceByS1");
            } else if (data.contains("PriceByS2")) {
                System.out.println("Save PriceByS2");
            } else if (data.contains("PriceByS3")) {
                System.out.println("Save PriceByS3");
            }
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exeScheme2() {
        Date start = new Date();
        S1Task s1Task = new S1Task();
        FutureTask<String> st1 = new FutureTask<>(s1Task);
        S2Task s2Task = new S2Task();
        FutureTask<String> st2 = new FutureTask<>(s2Task);
        S3Task s3Task = new S3Task();
        FutureTask<String> st3 = new FutureTask<>(s3Task);
        List<FutureTask<String>> futureTasks = Arrays.asList(st1, st2, st3);
        FutureTask<Boolean> saveTask = new FutureTask<>(new SaveTask(futureTasks));
        executor.submit(st1);
        executor.submit(st2);
        executor.submit(st3);
        executor.submit(saveTask);
        try {
            saveTask.get();
            System.out.println("询价结束：" + (new Date().getTime() - start.getTime()));
            executor.shutdown();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
