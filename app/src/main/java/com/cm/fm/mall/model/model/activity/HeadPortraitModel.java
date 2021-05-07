package com.cm.fm.mall.model.model.activity;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.common.HttpCallback;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.common.util.ImageUtil;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.task.VerifyTask;
import com.cm.fm.mall.contract.activity.HeadPortraitContract;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HeadPortraitModel implements HeadPortraitContract.Model {
    private String tag = "TAG_HeadPortraitModel";
    @Override
    public void saveHeadPortrait(String account, Bitmap bitmap, final Callback callback) {
        Map<String,String> map = new HashMap<>();
        map.put("account",account);
        String bitmapStr = ImageUtil.bitmapToString(bitmap);
        LogUtil.d(tag,"bitmapStr length: " + bitmapStr.length());
        map.put("headPortrait",bitmapStr);

        VerifyTask verifyTask = new VerifyTask(MallConstant.UPDATE_USER_INFO_URL, map, new HttpCallback() {
            @Override
            public void response(String response) {
                LogUtil.d(tag,"headPortrait save : " + response);
                if(TextUtils.isEmpty(response)){
                    callback.fail("更换头像失败");
                    return;
                }
                try {
                    JSONObject resJson = new JSONObject(response);
                    int code = resJson.getInt("code");
                    String msg = resJson.getString("msg");
                    if(code == 0){
                        callback.success(MallConstant.SUCCESS);
                        return;
                    }
                    callback.fail("更新头像失败");
                } catch (Exception e) {
                    LogUtil.e(tag,"解析失败");
                    callback.fail("更新头像失败");
                    e.printStackTrace();
                }

            }
        });
        verifyTask.execute();
    }
}
