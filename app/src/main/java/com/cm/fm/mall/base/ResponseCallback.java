package com.cm.fm.mall.base;

/**
 * 服务器验证响应的回调
 */
public interface ResponseCallback {
    void success(Object response);
    void fail(String info);
    void error(String error);
}
