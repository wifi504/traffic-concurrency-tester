package com.lhl.tct;

/**
 * 输出当前下载任务进度
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/8/12_14:52
 */
public class InfoDaemonThread implements Runnable {
    /**
     * 输出当前下载进度
     */
    public static void show() {
        double d = DownloadTask.getSize() / 1024.0 / 1024.0;
        double sizeMB = Math.round(d * 100) / 100.0;
        double sizeGB = sizeMB / 1024.0;
        double sizeTB = sizeGB / 1024.0;

        String out;
        if (sizeMB < 1024) {
            out = sizeMB + "MB";
        } else if (sizeGB < 1024) {
            out = sizeGB + "GB";
        } else {
            out = sizeTB + "TB";
        }
        System.out.println("当前累计下载：" + out);
    }

    /**
     * Runnable 每5秒输出一次进度
     */
    @Override
    public void run() {
        while (true) {
            show();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
