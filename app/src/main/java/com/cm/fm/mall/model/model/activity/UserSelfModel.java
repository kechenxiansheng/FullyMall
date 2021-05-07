package com.cm.fm.mall.model.model.activity;

import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.common.HttpCallback;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.task.VerifyTask;
import com.cm.fm.mall.contract.activity.UserSelfContract;
import com.cm.fm.mall.model.bean.UserInfo;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSelfModel implements UserSelfContract.Model {
    private String tag = "TAG_UserSelfModel";

    @Override
    public List<UserInfo> queryUserInfo() {
        return DataSupport.findAll(UserInfo.class);
    }

    @Override
    public void updateUserInfo(final UserInfo userInfo, final Callback callback) {
        final Map<String,String> params = new HashMap<>();
        params.put("account",userInfo.getName());
        params.put("nickName",userInfo.getNickName());
        params.put("phone",userInfo.getPhoneNumber());
        params.put("gender",userInfo.getSex()+"");
        VerifyTask verifyTask = new VerifyTask(MallConstant.UPDATE_USER_INFO_URL, params, new HttpCallback() {
            @Override
            public void response(String response) {
                try {
                    LogUtil.d(tag,"response : " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if(code==0){
                        //本地缓存
                        userInfo.save();
                        callback.success("保存成功");
                        return;
                    }
                    LogUtil.e(tag,"请求失败");
                    callback.fail("保存失败");
                } catch (Exception e) {
                    LogUtil.e(tag,"解析失败");
                    callback.fail("保存失败");
                    e.printStackTrace();
                }
            }
        });
        verifyTask.execute();
    }
}
