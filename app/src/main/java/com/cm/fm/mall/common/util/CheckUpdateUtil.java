package com.cm.fm.mall.common.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.webkit.MimeTypeMap;

import com.cm.fm.mall.BuildConfig;
import com.cm.fm.mall.R;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.view.dialog.AppUpdateDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * 检查更新工具类
 */
public class CheckUpdateUtil {

    private Handler handler;

    private final String TAG = "FM_CheckUpdateUtil";

    private static CheckUpdateUtil updateUtil;

    public static CheckUpdateUtil getInstance(){
        if(updateUtil==null){
            updateUtil = new CheckUpdateUtil();
        }
        return updateUtil;
    }
    //请求服务器获取版本信息
    public void checkUpdate(final Context context, final int actId){
        LogUtil.d(TAG,"checkUpdate start");
        //handler 在子线程中使用，需要创建 Looper 环境：Looper.prepare() Looper.loop();
        if(!Utils.isMainThread()){
            Looper.prepare();
        }
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                String result = (String)msg.obj;
                //展示更新提示框
                showUpdateDialog(context,result,actId);
                return false;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(MallConstant.VERSION_URL)
                        .build();
                Response response =null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG,"response exception");
//                    System.out.println(tag+"response exception");
                }finally {
                    LogUtil.e(TAG,"response is null?"+(response==null));
                }
                if(response!=null){
                    //服务器的版本信息
                    try {
                        String response_result = response.body().string();
                        LogUtil.e(TAG,"server response result:"+response_result);
                        Message message = new Message();
                        message.obj = response_result;
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                LogUtil.e(TAG,"checkUpdate end");
            }
        }).start();

        if(!Utils.isMainThread()){
            Looper.loop();
        }

    }

    /**
     * 根据服务器版本信息决定是否更新
     *  注意：replace 和 replaceAll 的区别
     *      都是替换所有的字符
     *      replace 不能使用正则表达式
     *      replaceAll 两个参数都支持正则表达式
     */
    public void showUpdateDialog(Context context,String res,int actId){
        LogUtil.d(TAG,"showUpdateDialog");
        try {
            //当前的版本号信息
            int vName = Integer.valueOf(BuildConfig.VERSION_NAME.replace(".",""));
            int vCode = BuildConfig.VERSION_CODE;
            LogUtil.d(TAG,"cur_version_code:"+ vCode +",cur_version_name:"+ vName);
            JSONObject jsonObject = new JSONObject(res);
            if(jsonObject.has("fullymall")){
                //服务器版本信息
                JSONObject fullymall_json = jsonObject.getJSONObject("fullymall");
                LogUtil.d(TAG,"fullymall_json:"+fullymall_json);
                String newVersionCode = fullymall_json.getString("newVersionCode");
                String newVersionName = fullymall_json.getString("newVersionName");
                String oldVersionCode = fullymall_json.getString("oldVersionCode");
                String oldVersionName = fullymall_json.getString("oldVersionName");
                String content = fullymall_json.getString("content");       //更新内容
                if(vName < Integer.valueOf(newVersionName.replace(".",""))){
                    //弹框
                    AppUpdateDialog tipDialog = new AppUpdateDialog(context,R.style.DialogTheme,newVersionName,content);
                    tipDialog.show();
                }else if(vName >= Integer.valueOf(newVersionName.replace(".",""))){
                    //用户中心界面点击检查更新，最新版本时弹一个提示
                    if(actId == 150){
                        Utils.tips(context,"已是最新版本");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 下载apk
     * DownloadManager：系统自带的下载工具
     * getExternalFilesDir()  需要配置fileProvider 的 external-files-path 属性
     */
    public void downloadApk(Context context){

        //File.separator 分隔符
        String local_apk_url = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator +"FullyMall.apk";
        LogUtil.d(TAG,"local_apk_url:"+local_apk_url);  // /storage/emulated/0/Android/data/com.cm.fm.mall/files/Download/FullyMall.apk
        File apk_file = new File(local_apk_url);//删除旧的apk
        if(apk_file.exists()){
            apk_file.delete();
        }
        Uri uri = Uri.parse(MallConstant.APK_URL);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        //设置是否允许漫游
        request.setAllowedOverRoaming(true);
        //根据文件获取MIME类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(local_apk_url));
        //设置文件类型，apk的类型：application/vnd.android.package-archive
        request.setMimeType(mimeString);
        //在通知栏中显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //允许被其他扫描器找到
        request.allowScanningByMediaScanner();
        request.setTitle("FullyMall正在更新");
        request.setVisibleInDownloadsUi(true);
        //指定下载位置，以及文件名称
        request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS, File.separator +"FullyMall.apk");
        // 将下载请求放入队列
        long download_id = downloadManager.enqueue(request);
        SharedPreferences preferences = context.getSharedPreferences("apk_update",MODE_PRIVATE);
        preferences.edit().putLong("download_id",download_id).apply();
        //TODO DownloadManager 下载完成会发送一条完成的广播 DownloadManager.ACTION_DOWNLOAD_COMPLETE（android.intent.action.DOWNLOAD_COMPLETE）
    }
}
