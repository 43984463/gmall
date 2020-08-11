package com.sherlock.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @auther Sherlock
 * @date 2020/8/11 21:36
 * @Description:
 */
@Slf4j
public class ThreadPoolMonitor extends ThreadPoolExecutor {

    public ThreadPoolMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, String poolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.startTimes = new ConcurrentHashMap<>();
        this.poolName = poolName;
        log.info("{} Going to init. PoolSize: {}, CorePoolSize: {}, waiting queue: {}",
                this.poolName, this.getPoolSize(), this.getCorePoolSize(), this.getQueue().size());
    }

    /**
     * 保存任务开始执行的时间，当任务结束时，用任务结束时间减去开始时间计算任务执行时间
     */
    private ConcurrentHashMap<String, Date> startTimes;

    /**
     * 线程池名称，一般以业务名称命名，方便区分
     */
    private String poolName;


    /**
     * 线程池延迟关闭时（等待线程池里的任务都执行完毕），统计线程池情况
     */
    @Override
    public void shutdown() {
        // 统计已执行任务、正在执行任务、未执行任务数量
        log.info("{} Going to shutdown. Executed tasks: {}, Running tasks: {}, Pending tasks: {}",
                this.poolName, this.getCompletedTaskCount(), this.getActiveCount(), this.getQueue().size());
        super.shutdown();
    }

    /**
     * 线程池立即关闭时，统计线程池情况
     */
    @Override
    public List<Runnable> shutdownNow() {
        // 统计已执行任务、正在执行任务、未执行任务数量
        log.info("{} Going to immediately shutdown. Executed tasks: {}, Running tasks: {}, Pending tasks: {}",
                this.poolName, this.getCompletedTaskCount(), this.getActiveCount(), this.getQueue().size());
        return super.shutdownNow();
    }

    /**
     * 任务执行之前，记录任务开始时间
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        startTimes.put(String.valueOf(r.hashCode()), new Date());
    }

    /**
     * 任务执行之后，计算任务结束时间
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        Date startDate = startTimes.remove(String.valueOf(r.hashCode()));
        Date finishDate = new Date();
        long diff = finishDate.getTime() - startDate.getTime();
        // 统计任务耗时、初始线程数、核心线程数、正在执行的任务数量、
        // 已完成任务数量、任务总数、队列里缓存的任务数量、池中存在的最大线程数、
        // 最大允许的线程数、线程空闲时间、线程池是否关闭、线程池是否终止
        log.info(LocalDateTime.now() + " {}-pool-monitor: " +
                        "Duration: {} ms, PoolSize: {}, CorePoolSize: {}, Active: {}, " +
                        "Completed: {}, Task: {}, Queue: {}, LargestPoolSize: {}, " +
                        "MaximumPoolSize: {},  KeepAliveTime: {}, isShutdown: {}, isTerminated: {}",
                this.poolName,
                diff, this.getPoolSize(), this.getCorePoolSize(), this.getActiveCount(),
                this.getCompletedTaskCount(), this.getTaskCount(), this.getQueue().size(), this.getLargestPoolSize(),
                this.getMaximumPoolSize(), this.getKeepAliveTime(TimeUnit.MILLISECONDS), this.isShutdown(), this.isTerminated());
    }
}