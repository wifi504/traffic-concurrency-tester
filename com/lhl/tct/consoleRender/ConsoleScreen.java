package com.lhl.tct.consoleRender;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 控制台（屏幕）类
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/8/15_14:32
 */
public class ConsoleScreen {

    private static ConsoleScreen cs = null;
    private static Thread renderThread = null;

    static Refresh ref = null;

    /**
     * 获取单例的屏幕对象<br /><br />
     * 调用对象的 turnON()、turnOFF() 方法以启用和关闭屏幕输出
     *
     * @param contents 渲染内容的包装对象
     * @return 屏幕实例
     */
    public static ConsoleScreen getScreen(Contents contents) {
        if (cs != null) {
            return cs;
        }
        cs = new ConsoleScreen(contents);
        renderThread = new Thread(new Render());
        renderThread.setName("Console-Screen-Render");
        return cs;
    }

    /**
     * 启动控制台显示屏
     */
    public void turnON() {
        renderThread.start();
    }

    /**
     * 停止控制台显示屏
     */
    public void turnOFF() {
        Render.enabled = false;
        renderThread.interrupt();
    }

    /**
     * 立即设置屏幕内容渲染频率<br /><br />
     * 接收值表示多少毫秒更新一次，默认值为1秒<br />
     * 你可以使用 Long.MAX_VALUE 来禁用屏幕内容自动更新，每次内容更新时手动使用 doRefresh() 进行更新<br /><br />
     * 注意：这是大概的刷新频率，实际速度取决于刷新回调的处理时间
     *
     * @param rate 刷新率
     */
    public static void setRefreshRate(long rate) {
        Render.sleepMillisecond = rate;
        if (renderThread != null) {
            renderThread.interrupt();
        }
    }

    /**
     * 设置更新回调方法<br /><br />
     * 每次屏幕显示更新自动调用此方法
     *
     * @param refresh 需要重写ref()方法
     */
    public static void onRefresh(Refresh refresh) {
        ref = refresh;
    }

    /**
     * 手动刷新一次显示
     */
    public static void doRefresh() {
        renderThread.interrupt();
    }

    // 构造方法
    private ConsoleScreen(Contents contents) {
        // 默认1秒更新一次
        setRefreshRate(1000);
        // 设置显示内容
        Render.render = contents.getContentList();
    }
}
