package com.lhl.tct;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lhl
 * @version 1.0
 * Create Time 2024/8/12_14:45
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("  ____________   ______          __           \n" +
                " /_  __/ ____/  /_  __/__  _____/ /____  _____\n" +
                "  / / / /        / / / _ \\/ ___/ __/ _ \\/ ___/\n" +
                " / / / /___     / / /  __(__  ) /_/  __/ /    \n" +
                "/_/  \\____/    /_/  \\___/____/\\__/\\___/_/     \n\n" +
                "流量并发性测试工具v1.0 (Traffic Concurrency Tester)\n" +
                "Author: WIFI连接超时\n" +
                "Github: https://github.com/wifi504/traffic-concurrency-tester \n" +
                "----------------------------------------------");

        // 检查用户是否指定了下载文件链接
        String fileURL = PropLoader.getFileURL();
        if ("".equals(fileURL)) {
            System.out.println("请在 config.properties 中设置下载地址后再次运行");
            return;
        }

        // 如果需要下载文件到本地，则准备存储文件夹
        File file = new File("temp");
        if (!file.exists() && !file.isDirectory() && PropLoader.isReallyDown()) {
            file.mkdir();
        }

        // 启动输出下载进度的守护线程
        Thread thread = new Thread(new InfoDaemonThread());
        thread.setDaemon(true);
        thread.start();

        // 提交下载任务
        ExecutorService executorService = Executors.newFixedThreadPool(PropLoader.getThreadNum());
        for (int i = 0; i < PropLoader.getCount(); i++) {
            executorService.submit(new DownloadTask(fileURL, file.getPath(), PropLoader.getReferer()));
        }
        executorService.shutdown();

        // 等待所有下载线程结束后退出 main 线程，并打印最终进度信息
        while (true) {
            if (executorService.isTerminated()) {
                InfoDaemonThread.show();
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
