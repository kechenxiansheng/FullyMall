package com.cm.fm.mall.model.model.activity;

import android.text.TextUtils;

import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.common.HttpCallback;
import com.cm.fm.mall.contract.activity.RegisterContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.VerifyTask;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * 注册的数据模型类
 */
public class RegisterModel implements RegisterContract.Model {
    private String tag = "TAG_RegisterModel";

    @Override
    public void registerM(final String account, final String password, final Callback callback) {
        /** 去服务器验证账号密码，并通过回调返回请求的结果 */
        VerifyTask verifyTask = new VerifyTask(MallConstant.REGISTER_VERIFY_URL,account, password, new HttpCallback() {
            @Override
            public void response(String response) {
                LogUtil.d(tag,"register verify: " + response);
                if(TextUtils.isEmpty(response)){
                    callback.fail("注册失败");
                    return;
                }
                try {
                    JSONObject resJson = new JSONObject(response);
                    int code = resJson.getInt("code");
                    String msg = resJson.getString("msg");
                    if(code == 0){
                        /** 新账号缓存在本地
                         * 本地缓存只保证存在一个账号信息，所以注册账号后，直接清除本地上一次的缓存
                         * */
                        int deleteAll = DataSupport.deleteAll(UserInfo.class);
                        LogUtil.d(tag,"last user cache delete: " + deleteAll);
                        UserInfo userInfo = new UserInfo();
                        userInfo.setName(account);
                        userInfo.setNickName(account);      //注册时昵称默认为账号
                        userInfo.setPassword(password);
                        userInfo.setUserType(MallConstant.USER_TYPE_NOT_LOGIN);
                        boolean res = userInfo.save();
                        LogUtil.d(tag,"register cache: " + res);
                        callback.success(MallConstant.SUCCESS);
                        return;
                    }
                    //注册失败
                    callback.fail(msg);
                }catch (Exception e){
                    LogUtil.e(tag,"其他错误");
                    callback.fail("注册失败");
                    e.printStackTrace();
                }
            }
        });
        verifyTask.execute();
    }
}
