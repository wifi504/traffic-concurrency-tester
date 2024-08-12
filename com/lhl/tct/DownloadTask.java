package com.lhl.tct;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author lhl
 * @version 1.0
 * Create Time 2024/8/12_14:53
 */
public class DownloadTask implements Runnable {

    private final String fileURL; // 下载文件链接
    private final String savePath; // 本地保存目录
    private final String header; // 携带的请求头

    private static int size; // 累计流量统计
    private static int count = 1; // 累计文件数统计

    /**
     * 下载任务构造方法
     *
     * @param fileURL  下载文件链接
     * @param savePath 本地保存目录
     * @param header   携带的请求头
     */
    public DownloadTask(String fileURL, String savePath, String header) {
        this.fileURL = fileURL;
        this.savePath = savePath;
        this.header = header;
    }

    /**
     * Runnable
     */
    @Override
    public void run() {
        try {
            downloadFile();
        } catch (IOException e) {
            System.out.println("下载失败：" + e.getMessage() + "; 目标文件：" + fileURL);
        }
    }

    /**
     * @return 当前累计流量
     */
    public static int getSize() {
        return size;
    }

    /**
     * 执行下载
     */
    public void downloadFile() throws IOException {
        // 请求目标文件信息
        URL url = new URL(fileURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        setRequestHeaders(httpURLConnection, header);
        int responseCode = httpURLConnection.getResponseCode();

        // 如果状态码为200则开始下载，否则输出错误码
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpURLConnection.getHeaderField("Content-Disposition");

            if (disposition != null) {
                // 从头部获取文件名
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10);
                }
            } else {
                // 从 URL 获取文件名
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            }
            fileName = "(" + count++ + ")" + fileName;
            fileName = fileName.replaceAll("[^a-zA-Z0-9.\\-()]", "");

            InputStream inputStream = httpURLConnection.getInputStream();
            FileOutputStream outputStream = null;
            if (PropLoader.isReallyDown()) {
                outputStream = new FileOutputStream(savePath + "/" + fileName);
            }

            int readCount = 0;
            byte[] bytes = new byte[4096];
            while ((readCount = inputStream.read(bytes)) != -1) {
                size += readCount;
                if (PropLoader.isReallyDown()) {
                    outputStream.write(bytes, 0, readCount);
                }
            }

            if (PropLoader.isReallyDown()) {
                outputStream.close();
            }
            inputStream.close();

            if (PropLoader.isReallyDown()) {
                System.out.println("文件已下载：" + fileName);
            }
        } else {
            System.out.println(responseCode + "错误，无法下载：" + fileURL);
        }
    }

    /**
     * 解析请求头内容并应用请求头
     *
     * @param httpCon 要增加请求头的 HttpURLConnection 对象
     * @param header  请求头
     */
    public static void setRequestHeaders(HttpURLConnection httpCon, String header) {
        String[] lines = header.split("\n");
        for (String line : lines) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                httpCon.setRequestProperty(key, value);
            }
        }
    }
}
