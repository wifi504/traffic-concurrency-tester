package com.lhl.tct.consoleRender;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 控制台渲染线程
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/8/15_13:27
 */
class Render implements Runnable {
    // 启用渲染
    static boolean enabled = true;

    // 渲染体
    static ArrayList<Content> render = new ArrayList<>();

    // 序列化输出
    static StringBuilder sb = new StringBuilder();

    // 更新周期
    static long sleepMillisecond = Long.MAX_VALUE;

    // 劫持系统标准输出流
    private static PrintStream renderOut = null;


    /**
     * 刷新控制台内容
     */
    private static void refresh() {
        // 更新内容
        if (ConsoleScreen.ref != null) {
            ConsoleScreen.ref.ref();
        }
        sb.delete(0, sb.length());
        render.forEach(content -> sb.append(content));

        // 清空控制台
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception ignored) {
        }

        // 输出新内容
        renderOut.println(sb);
    }


    /*
     * 时间戳转换
     * */
    public static String formatTimestamp(long l) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(l);
        return simpleDateFormat.format(date);
    }

    @Override
    public void run() {
        // 劫持标准输出流
        renderOut = System.out;
        PrintStream ps;
        try {
            ps = new PrintStream("system-log-" +
                    formatTimestamp(System.currentTimeMillis()) + ".txt");
            System.setOut(ps);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 开始渲染
        while (enabled) {
            refresh();
            try {
                Thread.sleep(sleepMillisecond);
            } catch (InterruptedException ignore) {
            }
        }
        refresh();

        // 释放标准输出流
        System.setOut(renderOut);
        ps.flush();
        ps.close();
    }
}