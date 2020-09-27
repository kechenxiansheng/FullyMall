package com.cm.fm.mall.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.bean.UserInfo;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.Utils;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 绑定手机界面
 */
public class BindPhoneActivity extends BaseActivity implements View.OnClickListener {
    private Activity context;
    private EditText et_register_bind_phoneNum,et_register_yzm;
    private TextView tv_bind_phone_bt,tv_bind_back;
    private TextView tv_register_get_yzm;
    private Handler mHandler;
    private EventHandler eventHandler;

    private final String tag = "TAG_BindPhoneActivity";
    private final String USER_COUNTRY = "86";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_bind_phone);
        //smssdk注册回调监听
        smssdkResgist();
        et_register_bind_phoneNum = findViewById(R.id.et_register_bind_phoneNum);   //手机号输入框
        tv_register_get_yzm = findViewById(R.id.tv_register_get_yzm);       //获取验证码按钮
        et_register_yzm = findViewById(R.id.et_register_yzm);               //验证码输入框
        tv_bind_phone_bt = findViewById(R.id.tv_bind_phone_bt);             //绑定按钮
        tv_bind_back = findViewById(R.id.tv_bind_back);             //绑定按钮


        tv_register_get_yzm.setOnClickListener(this);
        tv_bind_phone_bt.setOnClickListener(this);
        tv_bind_back.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消监听，防止内存泄露
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    @Override
    public void onClick(View v) {
        String phoneNum = et_register_bind_phoneNum.getText().toString();
        switch (v.getId()){
            case R.id.tv_register_get_yzm:
                //获取验证码
                if(phoneNum.isEmpty()){
                    Utils.getInstance().tips(context,"提示：号码不能为空！");
                    return;
                }
                //发送获取验证码请求
                SMSSDK.getVerificationCode(USER_COUNTRY,phoneNum);
                //倒计时开始
                TimeCount timeCount = new TimeCount(60000,1000);
                timeCount.start();
            case R.id.tv_bind_phone_bt:
                //点击绑定
                String yzm = et_register_yzm.getText().toString();
                if(yzm.isEmpty()){
                    Utils.getInstance().tips (context,"提示：请输入验证码！");
                    return;
                }
                //发送验证验证码的请求
                SMSSDK.submitVerificationCode(USER_COUNTRY,phoneNum,yzm);
                break;
            case R.id.tv_bind_back:
                context.finish();
                break;
        }
    }
    //验证码倒计时60秒
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture,long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //倒计时任务中
            tv_register_get_yzm.setClickable(false);
            tv_register_get_yzm.setText("("+millisUntilFinished / 1000 +") 秒后重试");
        }

        @Override
        public void onFinish() {
            //倒计时结束调用
            tv_register_get_yzm.setText("重新获取验证码");
            tv_register_get_yzm.setClickable(true);
        }
    }

    public void smssdkResgist(){
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Utils.getInstance().tips(context,"验证码验证成功!");
                    LogUtil.d(tag,"验证码验证成功！");
                    // 处理成功的结果
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                    // 国家代码，如“86”
                    String country = (String) phoneMap.get("country");
                    // 手机号码，如“13800138000”
                    String phone = (String) phoneMap.get("phone");

                    //保存用户信息
                    List<UserInfo> userInfos = DataSupport.findAll(UserInfo.class);
                    if(userInfos.size()!=0){
                        userInfos.get(0).setPhoneNumber(phone);
                        userInfos.get(0).save();
                        boolean res = userInfos.get(0).save();
                        LogUtil.d(tag,"save result::"+res);
                        LogUtil.d(tag,"cur_userInfo:"+userInfos.get(0).toString());
                        if(res){
                            setResult(RESULT_OK);
                            context.finish();
                        }
                    }

                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    Utils.getInstance().tips(context,"获取验证码成功，请查看短信！");
                    LogUtil.d(tag,"获取验证码成功，请查看短信！");
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                }
                return false;
            }
        });
        //smssdk注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }
}
