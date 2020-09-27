package com.cm.fm.mall.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.bean.UserInfo;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 修改密码的界面
 *  1、activityId : 5
 *  2、本页所有请求码以 500 开始
 */
public class UpdatepwdActivity extends BaseActivity implements View.OnClickListener {

    private UpdatepwdActivity context;
    ImageView iv_update_lock;
    TextView et_update_password1,et_update_password2,tv_update_tips,tv_update_get_yzm,tv_update_back;
    EditText et_update_phoneNum,et_update_yzm;
    Button bt_update_sure_and_login;
    private Handler mHandler;
    private EventHandler eventHandler;

    private boolean TypeIsPassword = true;
    private final String tag = "TAG_UpdatepwdActivity";
    private final String USER_COUNTRY = "86";
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //告知页面，使用动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
        setContentView(R.layout.activity_updatepwd);

        smssdkResgist();
        tv_update_back = findViewById(R.id.tv_update_back);
        iv_update_lock = findViewById(R.id.iv_update_lock);             //密码锁
        et_update_password1 = findViewById(R.id.et_update_password1);

        //TODO 密码框默认是密文类型，密文需与 TYPE_CLASS_TEXT 一起设置才生效
        et_update_password1.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        et_update_password2 = findViewById(R.id.et_update_password2);
        et_update_password2.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tv_update_tips = findViewById(R.id.tv_update_tips);
        tv_update_get_yzm = findViewById(R.id.tv_update_get_yzm);
        et_update_phoneNum = findViewById(R.id.et_update_phoneNum);
        et_update_yzm = findViewById(R.id.et_update_yzm);
        bt_update_sure_and_login = findViewById(R.id.bt_update_sure_and_login);

        iv_update_lock.setOnClickListener(this);
        tv_update_get_yzm.setOnClickListener(this);
        bt_update_sure_and_login.setOnClickListener(this);
        tv_update_back.setOnClickListener(this);


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消监听，防止内存泄露
        SMSSDK.unregisterEventHandler(eventHandler);
    }
    @Override
    public void onClick(View v) {
        String phoneNum = et_update_phoneNum.getText().toString();
        switch (v.getId()){
            case R.id.iv_update_lock:
                if(TypeIsPassword){
                    //如果当前 密码框 是密文类型，就替换为 文本明文 类型，并替换背景图片
                    et_update_password1.setInputType( InputType.TYPE_CLASS_TEXT);
                    iv_update_lock.setBackground(getResources().getDrawable(R.mipmap.ic_partial_secure));
                    TypeIsPassword = false;
                }else {
                    //设置为文本密码类型，并替换背景图片
                    et_update_password1.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iv_update_lock.setBackground(getResources().getDrawable(R.mipmap.ic_secure));
                    TypeIsPassword = true;
                }
                break;
            case R.id.tv_update_get_yzm:
                //获取验证码
                if(phoneNum.isEmpty()){
                    tv_update_tips.setText("提示：号码不能为空！");
                    return;
                }
                //发送获取验证码请求
                SMSSDK.getVerificationCode(USER_COUNTRY,phoneNum);
                //倒计时开始
                TimeCount timeCount = new TimeCount(60000,1000);
                timeCount.start();
            case R.id.bt_update_sure_and_login:
                //点击确认修改
                String pwd1= et_update_password1.getText().toString();
                String pwd2= et_update_password2.getText().toString();
                if(TextUtils.isEmpty(pwd1) || TextUtils.isEmpty(pwd2) || !pwd1.equals(pwd2)){
                    tv_update_tips.setText("提示：新密码输入有误！");
                    return;
                }

                String yzm = et_update_yzm.getText().toString();
                if(yzm.isEmpty()){
                    tv_update_tips.setText("提示：请输入验证码！");
                    return;
                }
                tv_update_tips.setText("提示：检测通过！");
                tv_update_tips.setTextColor(getResources().getColor(R.color.colorPrimary));

                //发送验证验证码的请求
                SMSSDK.submitVerificationCode(USER_COUNTRY,phoneNum,yzm);
                break;
            case R.id.tv_update_back:
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
            tv_update_get_yzm.setClickable(false);
            tv_update_get_yzm.setText("("+millisUntilFinished / 1000 +") 秒后重试");
        }

        @Override
        public void onFinish() {
            //倒计时结束调用
            tv_update_get_yzm.setText("重新获取验证码");
            tv_update_get_yzm.setClickable(true);
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
//                    Utils.getInstance().tips(context,"验证码验证成功!");
                    LogUtil.d(tag,"验证码验证成功！");

                    //保存用户信息
                    List<UserInfo> userInfos = DataSupport.findAll(UserInfo.class);
                    if(userInfos.size()!=0){
                        userInfos.get(0).setPassword(et_update_password1.getText().toString());
                        userInfos.get(0).save();
                        boolean res = userInfos.get(0).save();
                        LogUtil.d(tag,"save result::"+res);
                        LogUtil.d(tag,"cur_userInfo:"+userInfos.get(0).toString());
                        if(res){
                            Utils.getInstance().tips(context,"提示：修改成功！");
                            Utils.getInstance().startActivityClose(context,LoginActivity.class);
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
