package com.cm.fm.mall.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cm.fm.mall.MyApplication;

/**
 * 网络工具类
 * */
public class NetWorkUtil {
    private static NetWorkUtil utils;

    public static NetWorkUtil getInstance(){
        if(utils==null){
            utils = new NetWorkUtil();
        }
        return utils;
    }
    //判断是否有网络连接
    public boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }
    //判断Wifi是否可用
    public boolean isWifiConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWiFiNetworkInfo != null) {
            return mWiFiNetworkInfo.isAvailable();
        }
        return false;
    }
    //判断移动网路是否可用
    public boolean isMobileConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobileNetworkInfo != null) {
            return mMobileNetworkInfo.isAvailable();
        }
        return false;
    }
    //判断网络类型，移动网络：0   wifi：1  以太网（ethernet）：9   蓝牙：7   等等
    public static int getConnectedType() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
            return mNetworkInfo.getType();
        }
        return -1;
    }
}
