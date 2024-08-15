package com.lhl.tct;

import java.util.concurrent.*;

/**
 * 下载任务多线程管理器
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/8/15_17:18
 */
public class DownloadManager {
    private static ThreadPoolExecutor executor; // 线程池
    private static final int MAX_THREADS = PropLoader.getThreadNum(); // 最大线程数
    private static int targetThreads = 1; // 目标线程数
    private static double cacheSpeed = 0; // 缓存速度
    private static ScheduledExecutorService threadsMonitor; // 线程池监控


    public DownloadManager() {
        executor = new ThreadPoolExecutor(0, targetThreads,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardPolicy());

        threadsMonitor = Executors.newSingleThreadScheduledExecutor();
    }

    public ThreadPoolExecutor startDownloads() {
        for (long i = 0; i < PropLoader.getCount(); i++) {
            executor.submit(new DownloadTask(PropLoader.getFileURL(),
                    "temp", PropLoader.getReferer()));
        }
        monitor();
        return executor;
    }

    private void monitor() {

        threadsMonitor.scheduleAtFixedRate(() -> {
            // 当前速度没有超过最大速度，就加1线程数
            if (DownloadInfo.downSpeed < DownloadInfo.maxDownSpeedInAMinute
                    && targetThreads < MAX_THREADS) {
                targetThreads += 1;
            } else {
                // 一旦超过，保存现在这个速度，不加线程数，等一段时间
                cacheSpeed = DownloadInfo.downSpeed;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignore) {
                }
                // 发现速度比这个存的速度低了，说明适得其反了，最大线程数-5
                if (DownloadInfo.downSpeed < cacheSpeed) {
                    if (targetThreads > 5) {
                        targetThreads -= 5;
                    } else {
                        targetThreads = 1;
                    }
                }
            }
            executor.setMaximumPoolSize(targetThreads);

        }, 0, 1, TimeUnit.SECONDS);

    }

    public static int getActiveThreadsNum() {
        return executor.getActiveCount();
    }

    public static int getTargetThreads() {
        return targetThreads;
    }

    public static double getCacheSpeed() {
        return cacheSpeed;
    }
}
