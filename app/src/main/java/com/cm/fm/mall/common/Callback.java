package com.cm.fm.mall.common;

/**
 * 服务器验证响应的回调
 */
public interface Callback {
    void success(Object response);
    void fail(String info);
}
