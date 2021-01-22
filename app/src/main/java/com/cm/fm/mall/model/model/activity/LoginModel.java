package com.cm.fm.mall.model.model.activity;


import android.graphics.Bitmap;
import android.text.TextUtils;

import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.common.HttpCallback;
import com.cm.fm.mall.common.util.ImageUtil;
import com.cm.fm.mall.contract.activity.LoginContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.VerifyTask;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.cm.fm.mall.MyApplication.getContext;

/**
 * 登陆的数据模型类
 */
public class LoginModel implements LoginContract.Model {
    private String tag = "TAG_LoginModel";

    @Override
    public void loginM(final String account,final String password, final Callback callback) {
        /** 去服务器验证账号密码，并通过回调返回请求的结果 */
        Map<String,String> map = new HashMap<>();
        map.put("account",account);
        map.put("password",password);
        VerifyTask verifyTask = new VerifyTask(MallConstant.LOGIN_VERIFY_URL,map, new HttpCallback() {
            @Override
            public void response(String response) {
                LogUtil.d(tag,"login verify: " + response);
                if(TextUtils.isEmpty(response)){
                    callback.fail("登陆失败");
                    return;
                }
                try {
                    JSONObject resJson = new JSONObject(response);
                    int code = resJson.getInt("code");
                    String msg = resJson.getString("msg");
                    if(code == 0){
                        /** 修改当前账号在本地缓存的状态 */
                        String content = resJson.getString("content");
                        JSONObject contentJson = new JSONObject(content);
                        String nickName = contentJson.getString("nickName");
                        int gender = contentJson.getInt("gender");
                        String phone = contentJson.getString("phone");
                        String headPortrait = contentJson.getString("headPortrait");

                        List<UserInfo> userInfos = DataSupport.select("name","password")
                                .where("name=?",account)
                                .find(UserInfo.class);
                        LogUtil.d(tag,"login userInfos size:"+userInfos.size());
                        if(userInfos.size()!=0){
                            /* 当前账号有缓存，修改状态为在线 */
                            UserInfo userInfo = userInfos.get(0);
                            userInfo.setNickName(nickName);
                            userInfo.setSex(gender);
                            userInfo.setPhoneNumber(phone);
                            userInfo.setUserType(MallConstant.USER_TYPE_IS_LOGIN);
                            userInfo.save();
                        }else {
                            /* 当前账号本地没有缓存，先清除之前的缓存，在将当前账号缓存在本地 */
                            int deleteAll = DataSupport.deleteAll(UserInfo.class);
                            LogUtil.d(tag,"last user cache delete: " + deleteAll);
                            UserInfo userInfo = new UserInfo();
                            userInfo.setName(account);
                            userInfo.setNickName(TextUtils.isEmpty(nickName)?account:nickName);
                            userInfo.setSex(gender);
                            userInfo.setPhoneNumber(phone);
                            userInfo.setPassword(password);
                            userInfo.setUserType(MallConstant.USER_TYPE_IS_LOGIN);
                            boolean res = userInfo.save();
                            LogUtil.d(tag,"login cache: " + res);
                        }
                        if(!TextUtils.isEmpty(headPortrait)){
                            ImageUtil.saveHeadCache(ImageUtil.stringToBitmap(headPortrait));
                        }
                        callback.success(MallConstant.SUCCESS);
                        return;
                    }
                    //登陆失败
                    callback.fail(msg);
                }catch (Exception e){
                    callback.fail("登录失败");
                    e.printStackTrace();
                }
            }
        });
        verifyTask.execute();

    }


}
