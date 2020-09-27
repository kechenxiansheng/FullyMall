package com.cm.fm.mall.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.smssdk.gui.RegisterPage;

/**
 * 注册页面
 *  1、activityId : 3
 *  2、本页所有请求码以 300 开始
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private Activity context;

    private EditText et_register_account;
    private EditText et_register_password;
    private TextView tv_register_back;
    private Button bt_register_btn;
    private ImageView iv_register_imageView_lock;

    private final int USER_TYPE_IS_LOGIN = 1;
    private final int USER_TYPE_NOT_LOGIN = 0;
    public static final int REGISTER_ACTIVITY_ID = 3;

    private boolean TypeIsPassword = true;
    private final String tag = "TAG_RegisterActivity";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //告知页面，使用动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
        setContentView(R.layout.activity_register);
        context = this;
        et_register_account = findViewById(R.id.et_register_account);
        et_register_password = findViewById(R.id.et_register_password);
        tv_register_back = findViewById(R.id.tv_register_back);
        //TODO 密码框默认是密文类型，密文需与 TYPE_CLASS_TEXT 一起设置才生效
        et_register_password.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        bt_register_btn = findViewById(R.id.bt_register_btn);
        iv_register_imageView_lock = findViewById(R.id.iv_register_imageView_lock);

        LogUtil.d(tag,"onCreate");
        //密文明文背景图片切换
        iv_register_imageView_lock.setOnClickListener(this);
        //点击注册
        bt_register_btn.setOnClickListener(this);
        tv_register_back.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_register_imageView_lock:
                if(TypeIsPassword){
                    //如果当前 密码框 是密文类型，就替换为 文本明文 类型，并替换背景图片
                    et_register_password.setInputType( InputType.TYPE_CLASS_TEXT);
                    iv_register_imageView_lock.setBackground(getResources().getDrawable(R.mipmap.ic_partial_secure));
                    TypeIsPassword = false;
                }else {
                    //设置为文本密码类型，并替换背景图片
                    et_register_password.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iv_register_imageView_lock.setBackground(getResources().getDrawable(R.mipmap.ic_secure));
                    TypeIsPassword = true;
                }
                break;
            case R.id.bt_register_btn:
                //点击了注册按钮
                LogUtil.d(tag,"onClick");
                String account = et_register_account.getText().toString();
                String password = et_register_password.getText().toString();
//                if(TextUtils.isEmpty(account) || TextUtils.isEmpty(password)){
                if(account.isEmpty() || password.isEmpty()){
                    Utils.getInstance().tips (context,"提示：账号密码不能为空！");
                    return;
                }
                UserInfo userInfo = new UserInfo();
                userInfo.setName(account);
                userInfo.setNickName(account);      //注册时昵称默认为账号
                userInfo.setPassword(password);
                userInfo.setUserType(USER_TYPE_NOT_LOGIN);
                boolean res = userInfo.save();
                LogUtil.d(tag,"save result::"+res);
                if(res){
                    Utils.getInstance().startActivityClose(context,LoginActivity.class);
                }
                break;
            case R.id.tv_register_back:
                context.finish();
                break;
        }
    }

}
