package com.cm.fm.mall.model.model.activity;

import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.common.HttpCallback;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.common.task.VerifyTask;
import com.cm.fm.mall.contract.activity.BindPhoneContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.common.util.LogUtil;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindPhoneModel implements BindPhoneContract.Model {
    private String tag = "TAG_BindPhoneModel";
    @Override
    public void savePhoneM(String account,final String phoneNum, final Callback callback) {
        /** 账号绑定手机：保存手机号，并通过回调返回结果 */
        try {
            //保存用户信息
            Map<String, String> params = new HashMap<>();
            params.put("account", account);
            params.put("phone", phoneNum);
            VerifyTask verifyTask = new VerifyTask(MallConstant.UPDATE_USER_INFO_URL, params, new HttpCallback() {
                @Override
                public void response(String response) {
                    LogUtil.d(tag,"response : " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        String msg = jsonObject.getString("msg");
                        if(code == 0){
                            List<UserInfo> userInfos = DataSupport.findAll(UserInfo.class);
                            if(userInfos.size()!=0){
                                userInfos.get(0).setPhoneNumber(phoneNum);
                                boolean res = userInfos.get(0).save();
                                LogUtil.d(tag,"save result: "+res);
                                LogUtil.d(tag,"cur_userInfo: "+userInfos.get(0).toString());
                            }
                            callback.success("绑定成功");
                            return;
                        }
                        callback.fail("绑定失败");
                    } catch (Exception e) {
                        callback.fail("绑定失败");
                        e.printStackTrace();
                    }
                }
            });
            verifyTask.execute();


        } catch (Exception e) {
            LogUtil.d(tag,"其他错误");
            callback.fail("绑定失败");
            e.printStackTrace();

        }
    }
}
