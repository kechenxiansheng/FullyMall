package com.cm.fm.mall.model.model.activity;

import com.cm.fm.mall.contract.activity.UserSelfContract;
import com.cm.fm.mall.model.bean.UserInfo;

import org.litepal.crud.DataSupport;

import java.util.List;

public class UserSelfModel implements UserSelfContract.Model {
    @Override
    public List<UserInfo> queryUserInfo() {
        return DataSupport.findAll(UserInfo.class);
    }

    @Override
    public boolean updateUserInfo(UserInfo userInfo,String nickName,int sex) {
        userInfo.setNickName(nickName);
        userInfo.setSex(sex);
        return userInfo.save();
    }
}
