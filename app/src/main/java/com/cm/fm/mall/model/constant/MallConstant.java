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

    /** activity 与 fragment 固定常量 */
    //ShoppingCartActivity
    public static final int SHOPPING_CART_ACTIVITY_ACTIVITY_ID = 4;
    //MallFragment
    public static final int MALL_FRAGMENT_PERMISSION_REQUEST_CODE = 100;
    public static final int MALL_FRAGMENT_SEARCH_REQUEST_CODE = 101;
    //UserFragment
    public static final int USER_FRAGMENT_ACTIVITY_ID = 150;
    public static final int USER_FRAGMENT_LOGIN_REQUEST_CODE = 151;
    public static final int USER_FRAGMENT_USER_SELF_REQUEST_CODE = 152;
    public static final int USER_FRAGMENT_HEAD_PORTRAIT_REQUEST_CODE = 153;
    //BindPhoneActivity
    public static final int BIND_PHONE_ACTIVITY_REQUEST_CODE = 500;
    //HeadPortraitActivity
    public static final int HEAD_PORTRAIT_ACTIVITY_ALBUM_CODE = 601;
    public static final int HEAD_PORTRAIT_ACTIVITY_TAKE_PHOTO = 602;
    //AddressActivity
    public static final int ADDRESS_ACTIVITY_REQUEST_CODE = 700;
    public static final int ADDRESS_ACTIVITY_REQUEST_CODE_EDIT = 701;
    //AddressDetailActivity
    public static final int ADDRESS_DETAIL_ACTIVITY_CONTACT_CODE = 901;
    //ProductInfoFragment
    public static final int PRODUCT_INFO_FRAGMENT_SHOPPING_CART_REQUEST_CODE = 1000;
}
