package com.cm.fm.mall.model.model.activity;

import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.common.HttpCallback;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.common.task.VerifyTask;
import com.cm.fm.mall.contract.activity.UpdatePwdContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.common.util.LogUtil;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatePwdModel implements UpdatePwdContract.Model {
    private final String TAG = "FM_UpdatePwdModel";

    @Override
    public void savePwdM(String account, final String password, final Callback callback) {
        //先保存服务器
        Map<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("password", password);
        VerifyTask verifyTask = new VerifyTask(MallConstant.UPDATE_USER_INFO_URL, params, new HttpCallback() {
            @Override
            public void response(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if(code == 0){
                        //缓存在本地
                        List<UserInfo> userInfos = DataSupport.findAll(UserInfo.class);
                        if (userInfos.size() != 0) {
                            userInfos.get(0).setPassword(password);
                            userInfos.get(0).save();
                            boolean res = userInfos.get(0).save();
                            LogUtil.d(TAG, "save result : " + res);
                            LogUtil.d(TAG, "cur_userInfo : " + userInfos.get(0).toString());
                        }
                        callback.success("修改成功");
                        return;
                    }
                    callback.fail("修改密码失败");
                    LogUtil.e(TAG,"修改密码失败");
                } catch (Exception e) {
                    callback.fail("修改密码失败");
                    LogUtil.e(TAG,"修改密码失败");
                    e.printStackTrace();
                }
            }
        });
        verifyTask.execute();



    }
}
