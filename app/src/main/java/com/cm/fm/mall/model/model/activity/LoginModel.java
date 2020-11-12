package com.cm.fm.mall.model.model.activity;


import com.cm.fm.mall.base.ResponseCallback;
import com.cm.fm.mall.contract.activity.LoginContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 登陆的数据模型类
 */
public class LoginModel implements LoginContract.Model {
    private String tag = "TAG_LoginModel";

    @Override
    public void loginM(String account,String password,ResponseCallback callback) {
        /** 去服务器验证账号密码，并通过回调返回请求的结果 */
        try {
            //demo 直接使用的 litePal 存储数据
            List<UserInfo> userInfos = DataSupport.select("name","password")
                    .where("name=?",account)
                    .find(UserInfo.class);
            LogUtil.d(tag,"login userInfos size:"+userInfos.size());
            if(userInfos.size()!=0){
                LogUtil.d(tag,"login userInfo :"+ userInfos.get(0));
                callback.success(userInfos.get(0));
            }else {
                callback.fail("账号不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.error("请稍后再试");
        }

    }
}
