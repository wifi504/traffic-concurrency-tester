package com.lhl.tct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * 初始化时加载配置文件并提供静态读取方法
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/8/12_14:46
 */
public class PropLoader {

    private static final File file; // config.properties
    private static final HashMap<String, String> config = new HashMap<>(); // 配置文件键值对

    // 类加载时完成静态常量获取
    static {
        file = new File("config.properties");
        if (!file.isFile()) {
            try (FileOutputStream fos = new FileOutputStream(file); FileOutputStream referer = new FileOutputStream("referer.txt")) {
                String str = "#下载文件数量\ncount=10\n" + "#多线程启用数量\nthreadNum=5\n" + "#文件下载链接\nfileURL=\n" + "#是否保留下载文件到当前temp目录下\nreallyDownload=false\n\n\n" + "#若出现403等错误，请配置请求头到 referer.txt";
                fos.write(str.getBytes());
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("默认配置文件已生成！您可以使用文本编辑器修改 config.properties");
        }

        try (FileReader fr = new FileReader(file)) {
            Properties pro = new Properties();
            pro.load(fr);
            Enumeration<?> names = pro.propertyNames();
            while (names.hasMoreElements()) {
                String k = (String) names.nextElement();
                String v = pro.getProperty(k);
                config.put(k, v);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 下载任务数量
     */
    public static int getCount() {
        return Integer.parseInt(config.get("count"));
    }

    /**
     * @return 启用线程数
     */
    public static int getThreadNum() {
        return Integer.parseInt(config.get("threadNum"));
    }

    /**
     * @return 目标文件下载链接
     */
    public static String getFileURL() {
        return config.get("fileURL");
    }

    /**
     * @return 是否保存下载文件到本地
     */
    public static boolean isReallyDown() {
        return Boolean.parseBoolean(config.get("reallyDownload"));
    }

    /**
     * @return 加载自定义的请求头
     */
    public static String getReferer() {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader fr = new FileReader("referer.txt")) {
            int readCount = 0;
            char[] chars = new char[512];
            while ((readCount = fr.read(chars)) != -1) {
                stringBuilder.append(chars, 0, readCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
