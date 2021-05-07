package com.cm.fm.mall.broadcastreceiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.cm.fm.mall.BuildConfig;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.Utils;

import java.io.File;

import androidx.core.content.FileProvider;

import static android.content.Context.MODE_PRIVATE;

/**
 * 广播接收器 监听apk下载完成后进行安装
 */
public class ApkInstallReceiver extends BroadcastReceiver {

    private String tag = "TAG_InstallReceiver";
    String local_apk_url;
    SharedPreferences preferences;
    @Override
    public void onReceive(Context context, Intent intent) {
        //File.separator 分隔符
        local_apk_url = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+ File.separator +"FullyMall.apk";
        LogUtil.d(tag,"local_apk_url: "+local_apk_url);     //  /storage/emulated/0/Android/data/com.cm.fm.mall/files/Download/FullyMall.apk

        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            preferences = context.getSharedPreferences("apk_update",MODE_PRIVATE);
            long download_id = preferences.getLong("download_id",-1);
            if(downloadId == download_id){
                installApk(context);
            }
        } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            viewDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(viewDownloadIntent);
        }
    }

    private void installApk(Context context) {
        LogUtil.e(tag,"installApk start");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签，表示启动一个新的activity任务
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 26) {
            //TODO 如果在8.0以上，使用FileProvider，并先判断是否已经打开安装文件的权限
            boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                LogUtil.e(tag, "更新安装失败，请打开安装文件权限");
                Utils.tips(context,"提示：更新安装失败，请打开安装文件权限");
            }else {
                File file = new File(local_apk_url);
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                LogUtil.w(tag, "apkUri: " + apkUri.getPath());   // /my-apk-download/FullyMall.apk
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            }
        }else if(Build.VERSION.SDK_INT >= 24){
            //TODO Android 7.0以上 8.0以下 使用FileProvider
            File file = new File(local_apk_url);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            LogUtil.w(tag, "apkUri: " + apkUri.getPath());   // /my-apk-download/FullyMall.apk
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            //TODO Android 7.0以下 直接获取uri
            Uri apkUri = Uri.fromFile(new File(local_apk_url));
            LogUtil.w(tag,"apkUri: " + apkUri.getPath());
            intent.setDataAndType( apkUri , "application/vnd.android.package-archive");
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            LogUtil.e(tag,"installApk error");
            e.printStackTrace();
        }
        LogUtil.e(tag,"installApk end");
    }

}

