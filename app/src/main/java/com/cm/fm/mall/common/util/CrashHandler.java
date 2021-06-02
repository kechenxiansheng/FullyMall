package com.cm.fm.mall.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.view.dialog.CommonDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;

/**
 * 自定义 crash 异常捕获类
 * 系统提供了UncaughtExceptionHandler接口，可让我们自行捕获异常。当程序中有未被捕获的异常时，系统会自动调用 uncaughtException(Thread,Throwable) 方法,
 * Thread为出现未捕获异常的线程，throwable为未捕获的异常
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "FM_CrashHandler";
    private static CrashHandler sInstance = new CrashHandler();
    private static final boolean DEBUG = true;
    private Context mContext;

    private String time;
    private String logFilePath;
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".trace";
    private static Thread.UncaughtExceptionHandler mDefaultExceptionHandler;



    public CrashHandler() {
    }

    public static CrashHandler getInstance(){
        return sInstance;
    }

    /**
     * 初始化，application中
     * @param context 应用上下文
     */
    public void init(Context context){
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置为线程默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context;
    }
    /**
     * 当程序中有未被捕获的异常时，系统会自动调用 uncaughtException 方法，Thread为出现未捕获异常的线程，throwable为未捕获的异常
     * */
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable exception) {
        Log.d(TAG, "uncaughtException ");
        try {
            dumpExceptionToSDCard(exception);
            //异常日志上传至服务器
//            uploadExceptionToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        exception.printStackTrace();

        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则自己结束程序
        if(mDefaultExceptionHandler != null){
            Log.d(TAG, "uncaughtException 2");
            mDefaultExceptionHandler.uncaughtException(thread,exception);
        }else {
            Log.d(TAG, "uncaughtException 3");
            Process.killProcess(Process.myPid());
        }
    }

    //导出异常信息到sd卡中
    private void dumpExceptionToSDCard(Throwable exception){
        Log.d(TAG, "dumpExceptionToSDCard ");
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            if(DEBUG){
                Log.w(TAG, "sdcard unmounted,skip dump exception");
                return;
            }
        }
        String mPath = mContext.getExternalFilesDir("CrashTest").getPath()+"/log/";
        File dir = new File(mPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        long timeMillis = System.currentTimeMillis();
        time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeMillis));
        logFilePath = mPath + FILE_NAME + time + FILE_NAME_SUFFIX;
        // logFile : /storage/emulated/0/Android/data/com.cm.adp.demo/file/CrashTest/log/crash2021-05-19 14:49:34.trace
        Log.d(TAG, "logFile : " + logFilePath);
        File file = new File(logFilePath);
        //将crash信息和设备信息都放入log（时间+\n+设备信息+\n+异常信息）
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            writer.println(time);
            dumpPhoneInfo(writer);
            writer.println();
            //将异常写入PrintWriter中
            exception.printStackTrace(writer);
            writer.close();
            Log.d(TAG, "dumpExceptionToSDCard end");
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed ");
            e.printStackTrace();
        }

    }
    //导出设备和应用信息
    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        Log.d(TAG, "dumpPhoneInfo ");
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo pi = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        StringBuilder builder = new StringBuilder();
        //app版本号、android版本号、手机制造商、手机型号、CPU架构
        builder.append("App VersionName: ").append(pi.versionName).append(",VersionCode: ").append(pi.versionCode).append("\n")
                .append("OS Version: ").append(Build.VERSION.RELEASE).append(",TargetSdkVersion: ").append(Build.VERSION.SDK_INT).append("\n")
                .append("Vendor: ").append(Build.MANUFACTURER).append("\n")
                .append("Model: ").append(Build.MODEL).append("\n")
                .append("CPU ABI: ").append(Build.CPU_ABI).append(",ABI2: ").append(Build.CPU_ABI2);
        Log.w(TAG, "write : "+builder.toString());
        pw.println(builder.toString());
    }

    //在合适的时机，将异常信息上传至服务器
    private void uploadExceptionToServer(){
        Log.d(TAG, "uploadExceptionToServer ");
        File logFile = new File(logFilePath);
        if(logFile.exists()){
            Log.d(TAG, logFile.getName() + "存在！");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(logFile));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine())!=null){
                    sb.append(line).append("\n");
                }
//                Log.w(TAG, "read : " + sb.toString());
                final Map<String,String> data = new HashMap<>();
                data.put("deviceId",generateDeviceID());
                data.put("logInfo",sb.toString());

                //TODO 请求没有执行，待处理
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String response = HttpUtils.httpPost(MallConstant.CRASH_LOG_SUBMIT_URL, data);
                        Log.w(TAG, "crash log send result : "+response);
                    }
                }).start();
                Log.d(TAG, "uploadExceptionToServer 2");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "uploadExceptionToServer end");
    }

    //由于application中dialog弹窗无效，所以这个方法没用
    private void showTip(){
        CommonDialog dialog = new CommonDialog.Builder(mContext)
                .setContentTxt(time+":程序出现异常！给您带来的不便，深表歉意！为尽快优化解决问题，是否将异常信息发送给开发者？")
                .setSureText("发送")
                .setCancelText("不发送")
                .setChooseListener(new CommonDialog.ChooseListener() {
                    @Override
                    public void sure() {
                        Utils.tips(mContext,"感谢您的反馈！");
                        Log.w(TAG, "exception info has sent. ");
                    }
                    @Override
                    public void cancel() {
                        Log.w(TAG, "exception info canceled. ");
                    }
                })
                .build();
        dialog.show();
    }

    //安装一次，只缓存一个设备id
    private String generateDeviceID() {
        String uuid = "";
        if (uuid == null) {
            SharedPreferences prefs = mContext.getSharedPreferences("g_device_id.xml", 0);
            String id = prefs.getString("device_id", "");
            if (!TextUtils.isEmpty(id)) {
                uuid = id;
            } else {
                String androidId = Settings.Secure.getString(mContext.getContentResolver(), "android_id");
                try {
                    if (!"9774d56d682e549c".equals(androidId)) {
                        uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                    } else {
                        String deviceId = ((TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                        uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                prefs.edit().putString("device_id", uuid).apply();
            }
        }
        return uuid;
    }
}
