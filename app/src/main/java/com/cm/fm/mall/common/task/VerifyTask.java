package com.cm.fm.mall.common.task;

import android.os.AsyncTask;

import com.cm.fm.mall.common.HttpCallback;
import com.cm.fm.mall.common.util.HttpUtils;
import com.cm.fm.mall.common.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册，登陆验证异步类
 */
public class VerifyTask extends AsyncTask<Void,Void,String> {
    private final String TAG = "FM_VerifyTask";
    private String url;
    private Map<String,String> params;
    private HttpCallback callback;

    public VerifyTask(String url,Map<String,String> params ,HttpCallback callback) {
        this.url = url;
        this.params = params;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            return HttpUtils.httpPost(url, params);
        }catch (Exception e){
            LogUtil.e(TAG,"VerifyTask error");
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
       callback.response(result);
    }
}
