package com.cm.fm.mall.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
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

import java.util.List;

/**
 * 登陆界面
 *  1、activityId : 2
 *  2、本页所有请求码以 200 开始
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private Activity context;

    private EditText editText_account;
    private EditText editText_password;
    private Button btn_login;
    private ImageView imageView_lock;
    private TextView tv_login_tips,tv_forget_pwd,tv_login_back;

    private final int USER_TYPE_IS_LOGIN = 1;
    private final int USER_TYPE_NOT_LOGIN = 0;
    public static final int LOGIN_ACTIVITY_ID = 2;

    private boolean TypeIsPassword = true;
    private final String tag = "TAG_LoginActivity";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
        setContentView(R.layout.activity_login);

        editText_account = findViewById(R.id.account);
        editText_password = findViewById(R.id.password);
        tv_login_tips = findViewById(R.id.tv_login_tips);
        tv_forget_pwd = findViewById(R.id.tv_forget_pwd);
        tv_login_back = findViewById(R.id.tv_login_back);

        //TODO 密码框默认是密文类型，密文需与 TYPE_CLASS_TEXT 一起设置才生效
        editText_password.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btn_login = findViewById(R.id.login_btn);
        imageView_lock = findViewById(R.id.imageView_lock);

        LogUtil.d(tag,"onCreate");

        tv_login_back.setOnClickListener(this);
        imageView_lock.setOnClickListener(this);
        tv_forget_pwd.setOnClickListener(this);
        //点击登陆
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_lock:
                if(TypeIsPassword){
                    //如果当前 密码框 是密文类型，就替换为 文本明文 类型，并替换背景图片
                    editText_password.setInputType( InputType.TYPE_CLASS_TEXT);
                    imageView_lock.setBackground(getResources().getDrawable(R.mipmap.ic_partial_secure));
                    TypeIsPassword = false;
                }else {
                    //设置为文本密码类型，并替换背景图片
                    editText_password.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imageView_lock.setBackground(getResources().getDrawable(R.mipmap.ic_secure));
                    TypeIsPassword = true;
                }
                break;
            case R.id.login_btn:
                LogUtil.d(tag,"onClick login_btn");
                String account = editText_account.getText().toString();
                String password = editText_password.getText().toString();
                if(TextUtils.isEmpty(account) || TextUtils.isEmpty(password)){
                    Utils.getInstance().tips(context,"提示：账号密码不能为空！");
                    return;
                }
                List<UserInfo> userInfos = DataSupport.findAll(UserInfo.class);
                LogUtil.d(tag,"login userInfos size:"+userInfos.size());
                if(userInfos.size()!=0){
                    LogUtil.d(tag,"list userInfos:"+userInfos);
                    UserInfo cur_userInfo = userInfos.get(0);
                    if(account.equals(cur_userInfo.getName()) && password.equals(cur_userInfo.getPassword()) ) {
                        //修改登陆的状态
                        cur_userInfo.setUserType(USER_TYPE_IS_LOGIN);   //登陆态 正式用户
                        cur_userInfo.update(cur_userInfo.getId());
                        //如果是UserFragment 过来的回传数据
                        int activityId = getIntent().getIntExtra("activityId", 0);
                        LogUtil.d(tag,"activityId:"+ activityId);
                        if ( activityId == 100) {
                            setResult(RESULT_OK);
                            context.finish();
                            return;
                        }else if( activityId == ShoppingCartActivity.SHOPPING_CART_ACTIVITY_ID){
                            //关闭之前的购物车界面
                            ActivityCollector.removeActivity(ShoppingCartActivity.context);
                            //重新启动购物车界面
                            Utils.getInstance().startActivityClose(context,ShoppingCartActivity.class);
                            return;
                        }
                        //关闭之前的MainActivity
                        ActivityCollector.removeActivity(MainActivity.context);
                        //任何位置只要是 先注册在登录的都直接回到商城主界面
                        Utils.getInstance().startActivityCloseOther(context,MainActivity.class);
                        return;
                    }
                    tv_login_tips.setText("账号密码错误，请检查！");
                    tv_login_tips.setTextColor(getResources().getColor(R.color.colorAccent));

                }else {
                    tv_login_tips.setText("用户名不存在！");
                    tv_login_tips.setTextColor(getResources().getColor(R.color.colorAccent));
                }

                break;
            case R.id.tv_forget_pwd:
                Utils.getInstance().startActivityClose(context,UpdatepwdActivity.class);
                break;
            case R.id.tv_login_back:
                context.finish();
                break;
        }
    }
}
