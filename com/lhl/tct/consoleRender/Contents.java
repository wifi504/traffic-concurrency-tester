package com.lhl.tct.consoleRender;

import java.util.ArrayList;

/**
 * 渲染内容的包装对象
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/8/15_16:22
 */
public class Contents {
    private static ArrayList<Content> contents = null;
    private static Contents con = null;
    private static Content line = null;

    private Contents() {
        contents = new ArrayList<>();
        line = new Content("\n");
    }

    public static Contents getContents() {
        if (con != null) {
            return con;
        }
        con = new Contents();
        return con;
    }

    public Contents write(Content content) {
        contents.add(content);
        return con;
    }

    public Contents writeln(Content content) {
        contents.add(content);
        contents.add(line);
        return con;
    }

    public Contents writeln() {
        contents.add(line);
        return con;
    }

    ArrayList<Content> getContentList() {
        return contents;
    }
}
