package com.cm.fm.mall.model.model.activity;

import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.contract.activity.UpdatePwdContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.common.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class UpdatePwdModel implements UpdatePwdContract.Model{
    private String tag = "TAG_UpdatePwdModel";

    @Override
    public void savePwdM(String password, Callback callback) {
        //保存用户信息
        List<UserInfo> userInfos = DataSupport.findAll(UserInfo.class);
        if(userInfos.size()!=0){
            userInfos.get(0).setPassword(password);
            userInfos.get(0).save();
            boolean res = userInfos.get(0).save();
            LogUtil.d(tag,"save result : "+res);
            LogUtil.d(tag,"cur_userInfo : "+userInfos.get(0).toString());
            if(res){
                callback.success("修改成功");
            }else {
                callback.fail("修改失败");
            }
        }
    }
}
