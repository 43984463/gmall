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
         *
         * ************************************************************CompletableFuture 使用****************************************************************************
         *   一、
         *   CompletableFuture.runAsync()  返回值为Void
         *   CompletableFuture.supplyAsync() 返回值为定义的泛型类型
         *
         *
         *   二、whenComplete((BiConsumer<? super T, ? super Throwable> action))  可以处理正常和异常的计算结果，
         *   Lambda 表达式的第一个参数为泛型返回结果，第二个为异常
         *
         *   whenComplete 和 whenCompleteAsync:
         *   whenComplete： 执行当前任务的线程继续执行whenComplete里面的任务。
         *   whenCompleteAsync：把whenCompleteAsync 里面的任务交给线程池来执行
         *
         *   ******方法不以Async结尾，意味着Action使用相同的线程执行，而Async可能会使用其他线程执行(如果使用的是相同的线程池，则有可能使用同一个线程执行)******
         *
         *
         *   三、whenComplete 和 exceptionally, handle
         *   whenComplete 只能感知结果和异常，不能修改结果
         *   exceptionally 可以感知异常并重新返回
         *   handle 可以感知结果和异常，并且可以重新返回。 相当于以上结合
         *
         *   四、线程串行化执行
         *   thenApply方法：当一个线程依赖另一个线程时，获取上一个任务的执行结果，并返回当前任务的返回值。
         *   thenAccept方法： 消费处理结果。接收任务的处理结果，并消费处理，无返回结果。
         *   thenRun方法：只要上面的任务执行完成，就开始执行thenRun，只是处理完任务后，执行thenRun的后续操作。
         *
         *   带有Async默认是异步执行。 以上都需要前置任务成功完成。
         *   A thenRun B
         *   A成功执行完，再执行B   B不接收A的执行结果，并且B执行完无返回值
         *   A thenAccept B
         *   A成功执行完，再执行B   B接收A的执行结果，并且B执行完无返回值
         *   A thenApply B
         *   A成功执行完，再执行B   B接收A的执行结果，并且B执行完有返回值  返回结果类型以最后一次为准
         *
         *
         *   五、
         *   5.1 两任务组合（都要完成）
         *   runAfterBoth: 组合2个future，不需要获取future的结果，只需要2个future处理完成任务后，处理该任务。处理完没有返回值。
         *   thenAcceptBoth: 组合2个future，需要获取2个future任务的结果，然后处理该任务。处理完没有返回值。
         *   thenCombine: 组合2个future，需要获取2个future任务的结果，然后处理该任务。并返回当前任务的返回值。
         *   5.2 两任务组合（任意一个完成） （如果提交给线程池，还是会执行完所有任务）
         *   runAfterEither: 任意一个任务完成，不需要获取future的结果，只需要完成的任务future处理完成任务后，处理该任务。处理完没有返回值。
         *   acceptEither: 任意一个任务完成，需要获取future的结果，只需要完成的任务future处理完成任务后，处理该任务。处理完没有返回值。
         *   applyToEither: 任意一个任务完成，需要获取future的结果，只需要完成的任务future处理完成任务后，处理该任务。处理完并有返回值。
         *
         *   六、等待任务执行完之后再继续接下来的任务。
         *   6.1 多任务阻塞（都要完成）
         *   CompletableFuture<Void> future = CompletableFuture.allOf(future01, future02);
         *   future.get();
         *   这2行代码的作用是等(CompletableFuture.allOf(future01, future02))中的所有结果执行完再继续执行接下来的任务。
         *   如果没有在这阻塞，有可能参数里面的任务还都没执行完。
         *   future.get(); 仅仅负责阻塞。
         *
         *   6.2 任意任务完成阻塞（如果提交给线程池，还是会执行完所有任务）
         *   CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future01);
         *   anyOf.get();
         *   这2行代码的作用是等(CompletableFuture.allOf(future01, future02))中的任意结果执行完再继续执行接下来的任务。
         *   如果没有在这阻塞，有可能参数里面的任务都还没执行完。
         *   future.get(); 可以负责阻塞，并且获取执行完的返回结果。
         *
         *
         *
         *
         *   future.get()会阻塞线程直到获取到结果。
         *
         * ************************************************************CompletableFuture 使用****************************************************************************
         */


        /**
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("当前线程号 -> " + Thread.currentThread().getId());
            int n = 10 / 5;
            System.out.println("运行结果：" + n);
        }, executor); */


        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程号 -> " + Thread.currentThread().getId());
            int n = 10 / 0;
            return n;
        }, executor).whenComplete((result,excption) -> {
            System.out.println("运行结果：" + result + "异常：" + excption);
        }).exceptionally(throwable -> {
            return 10;
        });

        CompletableFuture<Void> future = CompletableFuture.allOf(future01);
        future.get();

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future01);
        anyOf.get();

        Integer integer = future01.get();
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
