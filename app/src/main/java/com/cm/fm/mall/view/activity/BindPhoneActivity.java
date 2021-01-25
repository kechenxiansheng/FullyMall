package com.cm.fm.mall.view.activity;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseMVPActivity;
import com.cm.fm.mall.contract.activity.BindPhoneContract;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.presenter.activity.BindPhonePresenter;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.Utils;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class BindPhoneActivity extends BaseMVPActivity<BindPhonePresenter> implements BindPhoneContract.View,View.OnClickListener {
    private Activity context;
    private EditText et_register_bind_phoneNum,et_register_yzm;
    private TextView tv_bind_phone_bt,tv_bind_back;
    private TextView tv_register_get_yzm,tv_code_tip;
    private Handler mHandler;
    private EventHandler eventHandler;
    private String account;
    private String phone;

    private final String tag = "TAG_BindPhoneActivity";
    private final String USER_COUNTRY = "86";
    @Override
    protected int initLayout() {
        context = this;
        account = getIntent().getStringExtra("account");
        LogUtil.d(tag,"account : " + account);
        return R.layout.activity_bind_phone;
    }

    @Override
    protected void activityAnim() {
        //使用进出场动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
    }

    @Override
    protected void initDataEnd() {
        //smssdk注册回调监听
        smssdkResgist();
    }

    @Override
    protected BindPhonePresenter createPresenter() {
        return new BindPhonePresenter();
    }

    @Override
    protected void initView() {

        et_register_bind_phoneNum = findViewById(R.id.et_register_bind_phoneNum);   //手机号输入框
        tv_register_get_yzm = findViewById(R.id.tv_register_get_yzm);       //获取验证码按钮
        et_register_yzm = findViewById(R.id.et_register_yzm);               //验证码输入框
        tv_bind_phone_bt = findViewById(R.id.tv_bind_phone_bt);             //绑定按钮
        tv_bind_back = findViewById(R.id.tv_bind_back);                     //绑定按钮
        tv_code_tip = findViewById(R.id.tv_code_tip);                       //验证码错误提示

        tv_register_get_yzm.setOnClickListener(this);
        tv_bind_phone_bt.setOnClickListener(this);
        tv_bind_back.setOnClickListener(this);
    }


    @Override
    public void OnSaveResult(int code, String msg) {
        switch (code){
            case MallConstant.SUCCESS:
                setResult(RESULT_OK);
                context.finish();
                break;
            case MallConstant.FAIL:
                Utils.getInstance().tips(context,"绑定失败");
                break;

        }
    }

    @Override
    public void onClick(View v) {
        String phoneNum = et_register_bind_phoneNum.getText().toString();
        switch (v.getId()){
            case R.id.tv_register_get_yzm:
                //点击 验证码
                if(phoneNum.isEmpty()){
                    Utils.getInstance().tips(context,"号码不能为空");
                    return;
                }
                /** 获取验证码 */
                SMSSDK.getVerificationCode(USER_COUNTRY,phoneNum);
                //清空提示
                tv_code_tip.setText("");
                //倒计时(单位：毫秒)
                BindPhoneActivity.TimeCount timeCount = new BindPhoneActivity.TimeCount(60000,1000);
                timeCount.start();
                break;
            case R.id.tv_bind_phone_bt:
                //点击绑定
                String yzm = et_register_yzm.getText().toString();
                if(TextUtils.isEmpty(yzm)){
                    Utils.getInstance().tips (context,"验证码为空");
                    return;
                }
                /** 校验拿到的验证码 */
                SMSSDK.submitVerificationCode(USER_COUNTRY,phoneNum,yzm);
                break;
            case R.id.tv_bind_back:
                context.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消监听，防止内存泄露
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    /** 验证码倒计时60秒 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture,long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //倒计时任务中
            tv_register_get_yzm.setClickable(false);
            tv_register_get_yzm.setText("("+millisUntilFinished / 1000 +") 秒");
        }

        @Override
        public void onFinish() {
            //倒计时结束调用
            tv_register_get_yzm.setText("验证码");
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
                    LogUtil.d(tag,"回调成功，data: " + data);
                    /** 验证成功的结果（ data: {phone=182xxxx2850, country=86} ） */
                    // 如果验证失败（会包含有 error 的信息：
                    // java.lang.Throwable: {"detail":"用户提交校验的验证码错误。","description":"需要校验的验证码错误","httpStatus":400,"status":468,
                    // "error":"The user submits the validation verification code error."}）
                    HashMap<String,Object> phoneMap = null;
                    try {
                        phoneMap = (HashMap<String, Object>) data;
                    } catch (Exception e) {
                        LogUtil.d(tag,e.getMessage());
                    }
                    if(phoneMap != null){
                        // 国家代码，如“86”
                        String country = (String) phoneMap.get("country");
                        // 手机号码，如“13800138000”
                        phone = (String) phoneMap.get("phone");
                        //保存手机号
                        mPresenter.savePhoneP(account,phone);
                    }else {
                        LogUtil.d(tag,"验证码错误");
                        tv_code_tip.setText("验证码错误");
                        tv_code_tip.setTextColor(getResources().getColor(R.color.colorAccent));

                    }
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    LogUtil.d(tag,"获取验证码成功");
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                }
                return false;
            }
        });
        //smssdk注册回调监听
        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
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
