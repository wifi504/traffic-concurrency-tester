package com.lhl.tct.consoleRender;

/**
 * 渲染内容类
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/8/15_14:22
 */

public class Content {
    // 渲染数据
    private String data;

    public Content(String data) {
        this.data = data;
    }

    public void setData(String s) {
        this.data = s;
    }

    @Override
    public String toString() {
        return this.data;
    }
}
