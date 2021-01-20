package com.cm.fm.mall.model.model.activity;

import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.contract.activity.BindPhoneContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.common.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class BindPhoneModel implements BindPhoneContract.Model {
    private String tag = "TAG_BindPhoneModel";
    @Override
    public void savePhoneM(String phoneNum, Callback callback) {
        /** 账号绑定手机：保存手机号，并通过回调返回结果 */
        try {
            //保存用户信息
            List<UserInfo> userInfos = DataSupport.findAll(UserInfo.class);
            if(userInfos.size()!=0){
                userInfos.get(0).setPhoneNumber(phoneNum);
                boolean res = userInfos.get(0).save();
                LogUtil.d(tag,"save result: "+res);
                LogUtil.d(tag,"cur_userInfo: "+userInfos.get(0).toString());
               if(res){
                   callback.success("绑定成功");
               }else {
                   callback.fail("绑定失败");
               }
            }
        } catch (Exception e) {
            LogUtil.d(tag,"其他错误");
            callback.fail("绑定失败");
            e.printStackTrace();

        }
    }
}
