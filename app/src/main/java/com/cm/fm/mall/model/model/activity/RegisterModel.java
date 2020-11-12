package com.cm.fm.mall.model.model.activity;

import com.cm.fm.mall.base.ResponseCallback;
import com.cm.fm.mall.contract.activity.RegisterContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.model.constant.MallConstant;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 注册的数据模型类
 */
public class RegisterModel implements RegisterContract.Model {
    private String tag = "TAG_RegisterModel";
    private final int USER_TYPE_IS_LOGIN = 1;
    private final int USER_TYPE_NOT_LOGIN = 0;
    @Override
    public void registerM(String account, String password, ResponseCallback callback) {
        /** 去服务器验证账号密码，并通过回调返回请求的结果 */
        try {
            //demo 直接使用的 litePal 存储数据
            List<UserInfo> userInfos = DataSupport.select("name","password")
                    .where("name=?",account)
                    .find(UserInfo.class);
            if(userInfos.size()!=0){
                callback.fail("账号已存在");
            }else {
                //保存数据
                UserInfo userInfo = new UserInfo();
                userInfo.setName(account);
                userInfo.setNickName(account);      //注册时昵称默认为账号
                userInfo.setPassword(password);
                userInfo.setUserType(USER_TYPE_NOT_LOGIN);
                boolean res = userInfo.save();
                if(res){
                    callback.success(MallConstant.SUCCESS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.error("请稍后再试");
        }
    }
}
