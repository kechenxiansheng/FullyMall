package com.cm.fm.mall.model.model.activity;

import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.common.util.HttpUtils;
import com.cm.fm.mall.contract.activity.UpdatePwdContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.common.util.LogUtil;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatePwdModel implements UpdatePwdContract.Model {
    private String tag = "TAG_UpdatePwdModel";

    @Override
    public void savePwdM(String account, String password, Callback callback) {
        //先保存服务器
        Map<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("password", password);
        try {
            String httpPost = HttpUtils.httpPost(MallConstant.UPDATE_USER_INFO_URL, params);
            JSONObject response = new JSONObject(httpPost);
            int code = response.getInt("code");
            String msg = response.getString("msg");
            if(code == 0){
                //缓存在本地
                List<UserInfo> userInfos = DataSupport.findAll(UserInfo.class);
                if (userInfos.size() != 0) {
                    userInfos.get(0).setPassword(password);
                    userInfos.get(0).save();
                    boolean res = userInfos.get(0).save();
                    LogUtil.d(tag, "save result : " + res);
                    LogUtil.d(tag, "cur_userInfo : " + userInfos.get(0).toString());
                    if (res) {
                        callback.success("修改成功");
                    } else {
                        callback.fail("修改失败");
                    }
                }
            }else {
                callback.fail("修改密码失败");
                LogUtil.e(tag,"修改密码失败");
            }
        } catch (Exception e) {
            callback.fail("修改密码失败");
            LogUtil.e(tag,"修改密码失败");
            e.printStackTrace();
        }

    }
}
