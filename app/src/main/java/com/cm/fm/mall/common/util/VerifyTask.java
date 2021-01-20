package com.cm.fm.mall.common.util;

import android.os.AsyncTask;

import com.cm.fm.mall.common.HttpCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册，登陆验证异步类
 */
public class VerifyTask extends AsyncTask<Void,Void,String> {
    private String url;
    private String account;
    private String password;
    private HttpCallback callback;

    public VerifyTask(String url,String account, String password,HttpCallback callback) {
        this.url = url;
        this.account = account;
        this.password = password;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        Map<String,String> params = new HashMap<>();
        params.put("account",account);
        params.put("password",password);
        try {
            return HttpUtils.httpPost(url, params);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
       callback.response(result);
    }
}
