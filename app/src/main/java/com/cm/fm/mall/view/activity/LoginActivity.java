package com.cm.fm.mall.view.activity;


import android.content.res.Configuration;
import android.os.LocaleList;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseMVPActivity;
import com.cm.fm.mall.contract.activity.LoginContract;
import com.cm.fm.mall.model.constant.MallConstant;
import com.cm.fm.mall.presenter.activity.LoginPresenter;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.Utils;
import com.cm.fm.mall.view.fragment.menu.UserFragment;

import androidx.annotation.NonNull;


/**
 * 登陆页
 * 注意：继承 BaseMVPActivity 时，需要传泛型类，绑定登陆的 presenter
 */
public class LoginActivity extends BaseMVPActivity<LoginPresenter> implements LoginContract.View,View.OnClickListener {
    private ProgressBar pb_progress;
    private EditText editText_account;
    private EditText editText_password;
    private Button btn_login;
    private ImageView imageView_lock;
    private TextView tv_login_tips,tv_forget_pwd,tv_login_back;

    public static final int USER_TYPE_IS_LOGIN = 1;

    private boolean TypeIsPassword = true;
    private final String tag = "TAG_LoginActivity";

    @Override
    protected LoginPresenter createPresenter() {
       return new LoginPresenter();
    }

    @Override
    protected void initView() {
        editText_account = findViewById(R.id.account);
        editText_password = findViewById(R.id.password);
        tv_login_tips = findViewById(R.id.tv_login_tips);    //账号密码错误提示
        tv_forget_pwd = findViewById(R.id.tv_forget_pwd);
        tv_login_back = findViewById(R.id.tv_login_back);
        pb_progress = findViewById(R.id.pb_progress_login);

        //TODO 密码框默认是密文类型，密文需与 TYPE_CLASS_TEXT 一起设置才生效
        editText_password.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btn_login = findViewById(R.id.btn_login);
        imageView_lock = findViewById(R.id.imageView_lock);
        btn_login = findViewById(R.id.btn_login);
        LogUtil.d(tag,"onCreate");

        tv_login_back.setOnClickListener(this);
        imageView_lock.setOnClickListener(this);
        tv_forget_pwd.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Configuration configuration = getResources().getConfiguration();

    }

    @Override
    protected void activityAnim() {
        //动画
        Utils.getInstance().actUseAnim(this,R.transition.fade);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_login;
    }

    /**
     * @param msg 登陆回传数据（成功：账号，失败：提示信息）
     */
    @Override
    public void OnLoginResult(int code,String msg) {
        if(code == MallConstant.SUCCESS){
            /** UserFragment 点击的登陆 */
            int activityId = getIntent().getIntExtra("activityId", 0);
            LogUtil.d(tag,"activityId:"+ activityId);
            if (activityId == MallConstant.USER_FRAGMENT_ACTIVITY_ID) {
                setResult(RESULT_OK);
                this.finish();
                return;
            }else if( activityId == MallConstant.SHOPPING_CART_ACTIVITY_ACTIVITY_ID){
                /** 购物车界面过来的请求（只是未登录），登陆成功直接关闭*/
//                ActivityCollector.finishOneActivity(ShoppingCartActivity.class.getName());
//                Utils.getInstance().startActivityClose(this,ShoppingCartActivity.class);
                this.finish();
                return;
            }
            /** 任何位置只要是 先注册在登录的都直接回到商城主界面 */
            Utils.getInstance().startActivityCloseAll(this,MainActivity.class);
        }else if(code == MallConstant.FAIL){
            tv_login_tips.setText(msg);
            tv_login_tips.setTextColor(getResources().getColor(R.color.colorAccent));
        }

    }

    @Override
    public void showLoading() {
        super.showLoading();
        pb_progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        pb_progress.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                LogUtil.d(tag,"onClick login_btn");
                String account = editText_account.getText().toString();
                String password = editText_password.getText().toString();
                if(TextUtils.isEmpty(account) || TextUtils.isEmpty(password)){
                    Utils.getInstance().tips(this,"提示：账号密码不能为空！");
                    return;
                }
                mPresenter.loginP(account,password);
                break;
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
            case R.id.tv_forget_pwd:
                Utils.getInstance().startActivityClose(this,UpdatePwdActivity.class);
                break;
            case R.id.tv_login_back:
                this.finish();
                break;
        }
    }
}
