package com.sherlock.gmall.search.thread;

import java.util.concurrent.*;

/**
 * @auther Sherlock
 * @date 2020/8/6 21:08
 * @Description:
 */
public class ThreadTest {

    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        System.out.println("main...start");
        /**
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("当前线程号 -> " + Thread.currentThread().getId());
            int n = 10 / 5;
            System.out.println("运行结果：" + n);
        }, executor); */


        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程号 -> " + Thread.currentThread().getId());
            int n = 10 / 0;
            return n;
        }, executor).whenComplete((result,excption) -> {
            System.out.println("运行结果：" + result + "异常：" + excption);
        }).exceptionally(throwable -> {
            return 10;
        });

        Integer integer = future.get();
        System.out.println("最终运行结果：" + integer);
        System.out.println("main...end");

    }





    public static void thread(String[] args) throws ExecutionException, InterruptedException {

        System.out.println("main... start");

        // 1、继承Thread
//        Thread01 thread01 = new Thread01();
//        thread01.start();

        // 2、实现Runnable接口
//        Runnable01 runnable01 = new Runnable01();
//        new Thread(runnable01).start();

        // 3、实现Callable接口 + FutureTask (可以拿到返回结果，可以处理异常)
//        FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
//        new Thread(futureTask).start();
        // 等待线程执行完获取结果
//        Integer n = futureTask.get();
//        System.out.println(n);

        // 线程池 --  资源控制
//        service.execute(new Runnable01());

        /**
         *   @see java.util.concurrent.ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit, java.util.concurrent.BlockingQueue, java.util.concurrent.ThreadFactory, java.util.concurrent.RejectedExecutionHandler)
         *
         *   创建线程池
         *
         *   七大参数：
         *   @param corePoolSize the number of threads to keep in the pool, even
         *          if they are idle, unless {@code allowCoreThreadTimeOut} is set
         *    核心线程数(一直存在，除非 java.util.concurrent.ThreadPoolExecutor#allowCoreThreadTimeOut);
         *       线程池创建好之后就准备了5个核心线程(底下的方法中指定)
         *   @param maximumPoolSize the maximum number of threads to allow in the
         *          pool
         *     200个自大核心线程数;可以控制资源重复利用
         *   @param keepAliveTime when the number of threads is greater than
         *          the core, this is the maximum time that excess idle threads
         *          will wait for new tasks before terminating.
         *     存活时间。 如果当前的线程数量大于core线程数，就会释放空闲线程（maximumPoolSize-corePoolSize）。
         *                        需要线程的空闲时间大于指定的时间(keepAliveTime)，核心线程不会释放。
         *   @param unit the time unit for the {@code keepAliveTime} argument
         *     时间单位。
         *   @param workQueue the queue to use for holding tasks before they are
         *          executed.  This queue will hold only the {@code Runnable}
         *          tasks submitted by the {@code execute} method.
         *     阻塞队列。如果任务很多。就会把目前多的任务放在队列里面。只要有线程空闲，就会去队列里面取出新的任务继续执行。
         *   @param threadFactory the factory to use when the executor
         *          creates a new thread
         *     线程的创建工厂。
         *   @param handler the handler to use when execution is blocked
         *          because the thread bounds and queue capacities are reached
         *     拒绝策略。如果队列满了，按照我们指定的拒绝策略拒绝执行任务。
         *   @throws IllegalArgumentException if one of the following holds:<br>
         *           {@code corePoolSize < 0}<br>
         *           {@code keepAliveTime < 0}<br>
         *           {@code maximumPoolSize <= 0}<br>
         *           {@code maximumPoolSize < corePoolSize}
         *   @throws NullPointerException if {@code workQueue}
         *           or {@code threadFactory} or {@code handler} is null
         *
         *
         *   工作顺序：
         *
         *   1）、线程池创建，准备好core数量的核心线程来准备接收任务。
         *   1.1）、core满了，就将再进来的任务放在阻塞队列里面。空闲的core线程会自己去阻塞队列中获取任务来执行。
         *   1.2）、阻塞队列满了，就直接开新线程执行，最大只能开到max的指定数量。
         *   1.3）、max满了就用 RejectedExecutionHandler 拒绝任务。
         *   1.4）、max都执行完成，有很多空闲，在指定的时间（keepAliveTime）以后，释放（maximumPoolSize-corePoolSize）数量的线程。
         *
         *      new LinkedBlockingDeque<>():默认是Integer.MAX_VALUE长度。 生产环境不能使用这个，有可能导致OOM。
         *
         *
         */

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());


        System.out.println("main... end");

    }

    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("Runnable01当前线程号 -> " + Thread.currentThread().getId());
            int n = 10 /5;
            return n;
        }

    }


    public static class Runnable01 implements Runnable {
        @Override
        public void run() {
            System.out.println("Runnable01当前线程号 -> " + Thread.currentThread().getId());
            int n = 10 /5;
            System.out.println(n);
        }
    }

    public static class Thread01 extends Thread{

        @Override
        public void run() {

            System.out.println("Thread01当前线程号 -> " + Thread.currentThread().getId());
            int n = 10 /5;
            System.out.println(n);
        }
    }
}
