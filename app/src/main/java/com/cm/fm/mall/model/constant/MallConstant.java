package com.cm.fm.mall.model.constant;

/**
 * 常量类
 */
public class MallConstant {
    /** 头像名称 */
    public final static String photo_name = "head_photo.jpg";
    /** apk下载地址 */
    public final static String apk_url = "http://119.27.160.230:8080/FullyMall.apk";
    /** 版本检测地址 */
    public final static String version_url = "http://119.27.160.230:8080/app_version.json";
    /** 视频地址 */
    public final static String video_url = "http://119.27.160.230:8080/video/yx.mp4";

    /** 商品详情图片地址 */
    public final static String detail_picture_url = "http://119.27.160.230:8080/product/detail/";   //再加上 图片数量的 .png
    public final static String detail_picture_url_end = ".jpg";   //再加上 图片数量的 .png

    /** 通用状态 */
    public static final int SUCCESS = 0;
    public static final int FAIL = -1;
    public static final int ERROR = -2;

}
