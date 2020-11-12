package com.cm.fm.mall.model.bean;

import org.litepal.crud.DataSupport;

/**
 * 搜索的历史记录
 */
public class SearchHistory extends DataSupport {
    int id;
    String msg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "SearchHistory{" +
                "id=" + id +
                ", msg='" + msg + '\'' +
                '}';
    }
}
