package com.cm.fm.mall.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.bean.UserInfo;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
/**
 * 用户信息页面
 * 1、activityId : 5
 * 2、本页所有请求码以 500 开始
 */
public class UserSelfActivity extends BaseActivity implements View.OnClickListener {
    private UserSelfActivity context;
    private final String tag = "TAG_UserSelfActivity";
    public static final int USER_SELF_ACTIVITY_ID = 5;

    EditText et_userself_name,et_userself_nickname;
    RadioButton rb_userself_sex_man,rb_userself_sex_woman;
    TextView tv_userself_phone,tv_userself_update,tv_userself_sure_update,tv_userself_back,tv_bind_phone_num;
    RadioGroup rg_userself_sex;
    private boolean SURE_UPDATE = false;    //false 此时确认修改按钮是隐藏状态  true 修改按钮按钮是显示状态
    List<UserInfo> userInfoList = new ArrayList<>();
    final int[] sexNum = {1};
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //告知页面，使用动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
        setContentView(R.layout.activity_user_self);

        et_userself_name = findViewById(R.id.et_userself_name);
        et_userself_nickname = findViewById(R.id.et_userself_nickname);
        rg_userself_sex = findViewById(R.id.rg_userself_sex);
        rb_userself_sex_man = findViewById(R.id.rb_userself_sex_man);
        rb_userself_sex_woman = findViewById(R.id.rb_userself_sex_woman);
        tv_userself_phone = findViewById(R.id.tv_userself_phone);
        tv_userself_update = findViewById(R.id.tv_userself_update);
        tv_userself_sure_update = findViewById(R.id.tv_userself_sure_update);
        tv_userself_back = findViewById(R.id.tv_userself_back);
        tv_bind_phone_num = findViewById(R.id.tv_bind_phone_num);

        queryUserInfo();
        tv_bind_phone_num.setOnClickListener(this);
        tv_userself_update.setOnClickListener(this);
        tv_userself_sure_update.setOnClickListener(this);
        tv_userself_back.setOnClickListener(this);
        //性别
        rg_userself_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                String sex = radioButton.getText().toString();
                if("男".equals(sex)){
                    sexNum[0] = 1;
                }else if("女".equals(sex)){
                    sexNum[0] = 2;
                }
            }
        });
    }
    private void queryUserInfo(){
        userInfoList = DataSupport.findAll(UserInfo.class);
        if(userInfoList.size()!=0){
            UserInfo userInfo = userInfoList.get(0);
            LogUtil.d(tag,"query userInfo:"+userInfo.toString());
            //给视图填充数据
            et_userself_name.setText(userInfo.getName());
            et_userself_nickname.setText(userInfo.getNickName());
            String phoneNum = userInfo.getPhoneNumber();
            LogUtil.d(tag,"phoneNum:"+phoneNum);
            String maskNumber = "";
            if(phoneNum!=null){
                tv_bind_phone_num.setText("更改");
                maskNumber = phoneNum.substring(0,3)+"****"+phoneNum.substring(7,phoneNum.length());
            }
            tv_userself_phone.setText(maskNumber);
            int sexNum = userInfo.getSex();
            if(sexNum==1){
                rb_userself_sex_man.setChecked(true);
            }else if(sexNum==2) {
                rb_userself_sex_woman.setChecked(true);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_bind_phone_num:
                //绑定手机
                Intent intent = new Intent(context,BindPhoneActivity.class);
                intent.putExtra("activityId",USER_SELF_ACTIVITY_ID);
                startActivityForResult(intent,500);
                break;
            case R.id.tv_userself_update:
                if(!SURE_UPDATE){
                    //点击修改信息，显示确认修改按钮,并让账号、昵称、性别可编辑
                    tv_userself_sure_update.setVisibility(View.VISIBLE);
                    //提示语更改为取消
                    tv_userself_update.setText("取消");
                    et_userself_nickname.setBackgroundColor(getResources().getColor(R.color.smssdk_white));
                    et_userself_nickname.setFocusableInTouchMode(true);
                    et_userself_nickname.setFocusable(true);//获取焦点

                    SURE_UPDATE = true;
                }else {
                    //提示语改回默认的 修改信息
                    tv_userself_update.setText("修改信息");
                    //隐藏确认修改按钮
                    tv_userself_sure_update.setVisibility(View.GONE);
                    //昵称编辑框 状态变回不可编辑
                    et_userself_nickname.setFocusableInTouchMode(false);
                    et_userself_nickname.setFocusable(false);
                    et_userself_nickname.setBackground(null);
                    SURE_UPDATE = false;
                }
                break;
            case R.id.tv_userself_sure_update:
                //点击确认修改按钮之后，隐藏确认修改按钮，并让账号、昵称不可编辑，失去焦点

                String nickName = et_userself_nickname.getText().toString();
                if(TextUtils.isEmpty(nickName.trim())){
                    Utils.getInstance().tips(context,"提示：昵称不能为空！");
                    return;
                }
                UserInfo userInfo = userInfoList.get(0);
                userInfo.setNickName(nickName);
                userInfo.setSex(sexNum[0]);
                userInfo.save();
                LogUtil.d(tag,"userInfo:"+userInfo.toString());
                userInfoList = DataSupport.findAll(UserInfo.class);
                tv_userself_sure_update.setVisibility(View.GONE);
                //修改按钮 提示语改回默认的 修改信息
                tv_userself_update.setText("修改信息");
                et_userself_nickname.setFocusableInTouchMode(false);
                et_userself_nickname.setFocusable(false);
                et_userself_nickname.setBackground(null);
                setResult(RESULT_OK);
                break;
            case R.id.tv_userself_back:
                context.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(tag, "onActivityResult requestCode:" + requestCode + ",resultCode:" + resultCode + ",data:" + data);
        switch (requestCode) {
            case 500:
                //TODO 显示手机号
                if (resultCode == Activity.RESULT_OK) {
                    userInfoList = DataSupport.findAll(UserInfo.class);
                    queryUserInfo();
                }
                break;
        }
    }
}
