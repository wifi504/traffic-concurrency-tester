package com.lhl.tct;

import java.util.HashMap;

/**
 * 输出当前下载任务进度
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/8/12_14:52
 */
public class DownloadInfo {

    static long startTimeMillis = System.currentTimeMillis();

    static double size;     // 以MB为单位的累计下载数据量，没有保留两位小数
    static double sizeMB;   // 以MB为单位的累计下载数据量
    static double sizeGB;   // 以GB为单位的累计下载数据量
    static double sizeTB;   // 以TB为单位的累计下载数据量

    static double downSpeed;        // 实时下载速度 (MB/s)
    static double avgDownSpeed;     // 平均下载速度 (MB/s)
    static double maxDownSpeedInAMinute; // 一分钟内的最大下载速度

    // 实时更新当前下载速度
    static {
        Thread computeCurrentDownSpeedThread = new Thread(() -> {
            while (true) {
                // 计算实时下载速度
                double d1 = DownloadTask.getSize();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                }
                double d2 = DownloadTask.getSize();
                downSpeed = d2 - d1;

                // 计算平均下载速度
                double totalTime = (System.currentTimeMillis() - startTimeMillis) / 1000.0;
                avgDownSpeed = DownloadTask.getSize() / totalTime;
            }
        });
        computeCurrentDownSpeedThread.setName("Current-DownloadSpeed-Computer");
        computeCurrentDownSpeedThread.setDaemon(true);
        computeCurrentDownSpeedThread.start();

        Thread computeAMinuteMaxDownSpeed = new Thread(() -> {
            HashMap<Long, Double> hashMap = new HashMap<>();
            while (true) {
                // 计算1分钟内的最大下载速度

                long current = System.currentTimeMillis();
                // 判断当前有没有超过一分钟的数据，有就删
                hashMap.keySet().removeIf(key -> key < current - (60 * 1000));
                // 把现在这个时间加入进去
                hashMap.put(current, downSpeed);
                // 把这里面最快的找出来赋值给maxDownSpeedInAMinute
                maxDownSpeedInAMinute = hashMap.values().stream().max(Double::compareTo).orElse(0.0);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignore) {
                }
            }
        });
        computeAMinuteMaxDownSpeed.setName("A-Minute-MaxDownloadSpeed-Computer");
        computeAMinuteMaxDownSpeed.setDaemon(true);
        computeAMinuteMaxDownSpeed.start();
    }

    /**
     * 输出当前下载进度
     */
    public static String getTotalSize() {
        String out;
        if (sizeMB < 1024) {
            out = sizeMB + "MB";
        } else if (sizeGB < 1024) {
            out = sizeGB + "GB (" + sizeMB + "MB)";
        } else {
            out = sizeTB + "TB (" + sizeGB + "GB)";
        }
        return "当前累计下载流量：" + out;
    }

    public static String getDownloadSpeed() {
        String out = "下载速度：";
        if (downSpeed < 1.0) {
            out += Math.round((downSpeed * 1024) * 100) / 100.0 + "KB/s(当前)";
        } else {
            out += Math.round(downSpeed * 100) / 100.0 + "MB/s(当前)";
        }
        out += "  ";
        if (avgDownSpeed < 1.0) {
            out += Math.round((avgDownSpeed * 1024) * 100) / 100.0 + "KB/s(平均)";
        } else {
            out += Math.round(avgDownSpeed * 100) / 100.0 + "MB/s(平均)";
        }
        out += maxDownSpeedInAMinute * 1024;
        return out;
    }

    public static void updateDownSize() {
        size = DownloadTask.getSize();
        sizeMB = Math.round(size * 100) / 100.0;
        sizeGB = Math.round(sizeMB / 1024.0 * 100) / 100.0;
        sizeTB = Math.round(sizeGB / 1024.0 * 100) / 100.0;
    }
}
