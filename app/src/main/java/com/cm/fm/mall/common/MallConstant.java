package com.cm.fm.mall.common;

/**
 * 常量类
 */
public class MallConstant {
    /** 头像名称 */
    public final static String PHOTO_NAME = "head_photo.jpg";
    /** 服务器url */
    public static final String serverUrl = "http://119.27.160.230:8888";
    /** apk下载地址 */
    public final static String APK_URL = serverUrl + "/apk/FullyMall.apk";
    /** 版本检测地址 */
    public final static String VERSION_URL = serverUrl + "/app_version.json";
    /** 视频地址 */
    public final static String VIDEO_URL = serverUrl + "/resource/video/yc.mp4";
    /** 分类页 临时图片 */
    public final static String CLASSIFY_ICON = serverUrl + "/resource/image/icon/icon_dog.png";

    /** 注册验证 */
    public final static String REGISTER_VERIFY_URL = serverUrl + "/fmserver/user/register";
    /** 登陆验证 */
    public final static String LOGIN_VERIFY_URL = serverUrl + "/fmserver/user/login";
    /** 更新用户信息 */
    public final static String UPDATE_USER_INFO_URL = serverUrl + "/fmserver/user/updateUserInfo";
    /** 异常信息上报 */
    public final static String CRASH_LOG_SUBMIT_URL = serverUrl + "/fmserver/crash/submit";

    /** 商品详情图片地址 */
    public final static String DETAIL_PICTURE_URL = serverUrl + "/resource/product/detail/";   //再加上 图片数量的 .png
    public final static String DETAIL_PICTURE_URL_END = ".jpg";   //再加上 图片数量的 .png

    /** 通用状态 */
    public static final int SUCCESS = 0;
    public static final int FAIL = -1;
    public static final int ERROR = -2;

    /** 用户登陆状态 1 在线  0 离线*/
    public static final int USER_TYPE_IS_LOGIN = 1;
    public static final int USER_TYPE_NOT_LOGIN = 0;


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
    public static final int USER_FRAGMENT_REGISTER_REQUEST_CODE = 154;
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
