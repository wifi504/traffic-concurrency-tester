package com.lhl.tct;

import com.lhl.tct.consoleRender.ConsoleScreen;
import com.lhl.tct.consoleRender.Content;
import com.lhl.tct.consoleRender.Contents;
import com.lhl.tct.consoleRender.Refresh;

import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author lhl
 * @version 1.0
 * Create Time 2024/8/12_14:45
 */
public class Main {

    // 准备要显示的内容
    static Content header = new Content(
            "  ____________   ______          __           \n" +
                    " /_  __/ ____/  /_  __/__  _____/ /____  _____\n" +
                    "  / / / /        / / / _ \\/ ___/ __/ _ \\/ ___/\n" +
                    " / / / /___     / / /  __(__  ) /_/  __/ /    \n" +
                    "/_/  \\____/    /_/  \\___/____/\\__/\\___/_/     \n\n" +
                    "流量并发性测试工具v1.1 (Traffic Concurrency Tester)\n" +
                    "Author: WIFI连接超时\n" +
                    "Github: https://github.com/wifi504/traffic-concurrency-tester \n" +
                    "----------------------------------------------");

    static Content hr = new Content("--------------------------");
    static Content startTime = new Content("流量并发测试已启动！(2024-08-15 14:14:10)");
    static Content downloadMode = new Content("当前取流模式：内存中立即丢弃");
    static Content downSpeed = new Content("当前下载速度：0MB/s");
    static Content totalDownSize = new Content("当前累计下载流量：0MB");
    static Content totalDownCount = new Content("当前累计下载文件个数：0  目标个数：0  进度：0%");
    static Content threadsStatus = new Content("当前下载线程数：0  最高0");
    static Content temp = new Content("");

    public static void main(String[] args) {

        Contents contents = Contents.getContents();
        contents.writeln(header).writeln(startTime).writeln(downloadMode)
                .writeln(hr).writeln(totalDownSize).writeln(threadsStatus).writeln(downSpeed).writeln(temp);
        ConsoleScreen screen = ConsoleScreen.getScreen(contents);
        ConsoleScreen.setRefreshRate(300);
        ConsoleScreen.onRefresh(new Refresh() {
            @Override
            public void ref() {
                // 当前累计下载流量
                DownloadInfo.updateDownSize();
                totalDownSize.setData(DownloadInfo.getTotalSize());

                // 当前下载速度
                downSpeed.setData(DownloadInfo.getDownloadSpeed());

                // 当前下载线程信息
                threadsStatus.setData("当前下载线程数：" + DownloadManager.getActiveThreadsNum()
                        + "  最高：" + PropLoader.getThreadNum() + "  目标：" + DownloadManager.getTargetThreads());

                // 临时输出
                temp.setData("DownloadManager.cacheSpeed=" + DownloadManager.getCacheSpeed() + "\n");
            }
        });
        screen.turnON();

        // 检查用户是否指定了下载文件链接
        String fileURL = PropLoader.getFileURL();
        if ("".equals(fileURL)) {
            contents.write(new Content("请在 config.properties 中设置下载地址后再次运行\n\n\n"));
            screen.turnOFF();
            return;
        }


        // 如果需要下载文件到本地，则准备存储文件夹
        File file = new File("temp");
        if (!file.exists() && !file.isDirectory() && PropLoader.isReallyDown()) {
            file.mkdir();
            downloadMode.setData("当前取流模式：下载到temp目录下");
        }


        // 提交下载任务
//        ExecutorService executorService = Executors.newFixedThreadPool(PropLoader.getThreadNum());
//        for (long i = 0; i < PropLoader.getCount(); i++) {
//            executorService.submit(new DownloadTask(fileURL, file.getPath(), PropLoader.getReferer()));
//        }
//        executorService.shutdown();
        ThreadPoolExecutor threadPoolExecutor = new DownloadManager().startDownloads();

        // 等待所有下载线程结束后退出 main 线程，并打印最终进度信息
        while (true) {
            if (threadPoolExecutor.isTerminated()) {
                screen.turnOFF();
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
