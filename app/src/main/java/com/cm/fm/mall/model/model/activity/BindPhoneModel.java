package com.cm.fm.mall.model.model.activity;

import com.cm.fm.mall.base.ResponseCallback;
import com.cm.fm.mall.contract.activity.BindPhoneContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class BindPhoneModel implements BindPhoneContract.Model {
    private String tag = "TAG_BindPhoneModel";
    @Override
    public void savePhoneM(String phoneNum, ResponseCallback callback) {
        /** 账号绑定手机：保存手机号，并通过回调返回结果 */
        try {
            //保存用户信息
            List<UserInfo> userInfos = DataSupport.findAll(UserInfo.class);
            if(userInfos.size()!=0){
                userInfos.get(0).setPhoneNumber(phoneNum);
                boolean res = userInfos.get(0).save();
                LogUtil.d(tag,"save result::"+res);
                LogUtil.d(tag,"cur_userInfo:"+userInfos.get(0).toString());
               if(res){
                   callback.success("绑定成功");
               }else {
                   callback.fail("绑定失败");
               }
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.error("请稍后再试");
        }
    }
}
