package com.cm.fm.mall.view.fragment.menu;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.common.util.ImageUtil;
import com.cm.fm.mall.presenter.fragment.UserPresenter;
import com.cm.fm.mall.view.activity.AddressActivity;
import com.cm.fm.mall.view.activity.HeadPortraitActivity;
import com.cm.fm.mall.view.activity.LoginActivity;
import com.cm.fm.mall.view.activity.RegisterActivity;
import com.cm.fm.mall.view.activity.ShoppingCartActivity;
import com.cm.fm.mall.view.activity.UserSelfActivity;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.view.dialog.AgreementDialog;
import com.cm.fm.mall.base.BaseMVPFragment;
import com.cm.fm.mall.common.util.CheckUpdateUtil;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.NetWorkUtil;
import com.cm.fm.mall.common.util.Utils;
import com.cm.fm.mall.view.dialog.CommonDialog;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * 展示用户信息的页面，（隶属于 MainActivity）
 *  * 1、activityId : 100
 *  * 2、本页所有请求码以 150 开始（注意和 MainActivity 的不要重复）
 *  * 3、fragment 中直接使用startActivityForResult() ,不可用getActivity().startActivityForResult() 。
 *      否则 fragment 中重写的 onActivityResult 会收不到回传数据。
 *      因为优先回传给父类了，也就是 MainActivity 的 onActivityResult
 * */
public class UserFragment extends BaseMVPFragment<UserPresenter> implements View.OnClickListener {
    private Activity context;

    private List<UserInfo> userInfos = new ArrayList<>();
    private boolean typeOflogin;        //true 登陆（文本显示的注销）  false 注销（文本显示的登陆）

    private ImageView iv_head_portrait; //头像
    private String account;             //当前用户账号

    private TextView tv_nick_name,tv_tips_login_logout;          //昵称、点击登陆(注销)、绑定电话的描述、绑定手机
    private LinearLayout ll_user_info,ll_user_order,ll_user_address,ll_user_check,ll_user_shopping_cart,ll_user_agreement,ll_exception_test;    //个人信息、订单信息、地址信息、检查更新、购物车/用户协议
    private final String TAG = "FM_UserFragment";


    @Override
    public int initLayout() {
        //加载视图
        return R.layout.fragment_user;
    }

    @Override
    protected UserPresenter createPresenter() {
        return new UserPresenter();
    }

    @Override
    public void initView(View view) {
        context = getActivity();
        //子控件初始化
        iv_head_portrait = view.findViewById(R.id.iv_head_portrait);
        tv_nick_name = view.findViewById(R.id.tv_nick_name);
        ll_user_info = view.findViewById(R.id.ll_user_info);
        ll_user_order = view.findViewById(R.id.ll_user_order);
        ll_user_address = view.findViewById(R.id.ll_user_address);
        tv_tips_login_logout = view.findViewById(R.id.tv_tips_login_logout);
        ll_user_check = view.findViewById(R.id.ll_user_check);
        ll_user_shopping_cart = view.findViewById(R.id.ll_user_shopping_cart);
        ll_user_agreement = view.findViewById(R.id.ll_user_agreement);
        ll_exception_test = view.findViewById(R.id.ll_exception_test);

        iv_head_portrait.setOnClickListener(this);
        ll_user_info.setOnClickListener(this);
        ll_user_order.setOnClickListener(this);
        ll_user_address.setOnClickListener(this);
        tv_tips_login_logout.setOnClickListener(this);
        ll_user_check.setOnClickListener(this);
        ll_user_shopping_cart.setOnClickListener(this);
        ll_user_agreement.setOnClickListener(this);
        ll_exception_test.setOnClickListener(this);

        //先查询玩家信息
        refreshUserInfo();
        //展示头像
        showHeadPhoto();
        if(userInfos.size() != 0 && userInfos.get(0).getUserType()==1){
            tv_nick_name.setText(userInfos.get(0).getNickName());
            //已经登录,文本显示修改为注销
            typeOflogin = true;
            tv_tips_login_logout.setText(getResources().getString(R.string.user_logout_des));
        }
    }


    @Override
    public void dataDestroy() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG,"onActivityResult requestCode:"+requestCode+",resultCode:"+resultCode+",data:"+data);
        switch (requestCode){
            case MallConstant.USER_FRAGMENT_HEAD_PORTRAIT_REQUEST_CODE:
                /** 头像页回调 */
                if(resultCode == RESULT_OK){
                   showHeadPhoto();
                }
                break;
            case MallConstant.USER_FRAGMENT_USER_SELF_REQUEST_CODE:
                /** 个人资料回调 */
                if(resultCode == RESULT_OK){
                    refreshUserInfo();
                    LogUtil.d(TAG,"change nickname. userInfos:"+userInfos);
                    tv_nick_name.setText(userInfos.get(0).getNickName());
                }
                break;
            case MallConstant.USER_FRAGMENT_LOGIN_REQUEST_CODE:
                /** 登陆回调 */
                if(resultCode == RESULT_OK){
                    if(data!=null){
                        //登陆页点击了注册场景走这步
                        String type = data.getStringExtra("type");
                        LogUtil.d(TAG,"type : " + type);
                        if("register".equals(type)){
                            //打开注册页面
                            Intent intent = new Intent(context,RegisterActivity.class);
                            intent.putExtra("activityId",MallConstant.USER_FRAGMENT_ACTIVITY_ID);
                            startActivityForResult(intent,MallConstant.USER_FRAGMENT_REGISTER_REQUEST_CODE,ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
                        }
                    }else{
                        //直接登陆场景走这步
                        refreshUserInfo();
                        LogUtil.d(TAG,"login success. userInfos:"+userInfos);
                        //显示头像、昵称
                        tv_nick_name.setText(userInfos.get(0).getNickName());
                        //已经登录，文本显示修改为注销
                        typeOflogin = true;
                        //展示头像
                        showHeadPhoto();
                        tv_tips_login_logout.setText(getResources().getString(R.string.user_logout_des));
                    }
                }
                break;
            case MallConstant.USER_FRAGMENT_REGISTER_REQUEST_CODE:
                /** 注册页面的回调 */
                if(resultCode == RESULT_OK){
                    if(data!=null){
                        //点击了登陆场景走这步
                        String type = data.getStringExtra("type");
                        LogUtil.d(TAG,"type : " + type);
                        if("login".equals(type)){
                            //打开登陆页面
                            Intent intent = new Intent(context,LoginActivity.class);
                            intent.putExtra("activityId",MallConstant.USER_FRAGMENT_ACTIVITY_ID);
                            startActivityForResult(intent,MallConstant.USER_FRAGMENT_LOGIN_REQUEST_CODE,ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
                        }
                    }else {
                        //直接注册场景走这步。直接登陆，显示用户信息
                        refreshUserInfo();
                        if(userInfos.size() != 0 && userInfos.get(0).getUserType()==1){
                            tv_nick_name.setText(userInfos.get(0).getNickName());
                            //已经登录,文本显示修改为注销
                            typeOflogin = true;
                            tv_tips_login_logout.setText(getResources().getString(R.string.user_logout_des));
                        }
                    }
                }
                break;
        }
    }
    //展示头像
    private void showHeadPhoto(){
        // File.separator 文件目录间隔符
        String path = context.getExternalFilesDir(DIRECTORY_PICTURES) + File.separator + MallConstant.PHOTO_NAME;
        LogUtil.d(TAG,"get photo path : " + path);
        File file = new File(path);
        Bitmap bitmap;
        if(userInfos.size()!=0 && userInfos.get(0).getUserType()==1 && file.exists()){
            //已经登录，有头像直接展示
            LogUtil.d(TAG,"photo name :"+file.getName());
            bitmap = BitmapFactory.decodeFile(path);
        }else {
            //使用默认图片
            bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.head_photo);
        }
        bitmap = ImageUtil.createCircleBitmap(bitmap);
        iv_head_portrait.setImageBitmap(bitmap);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_head_portrait:
                //点击头像
                if(userInfos.size()==0 || userInfos.get(0).getUserType()==0){
                    Utils.tips(context,"请登陆！");
                    return;
                }
                Intent intent_head = new Intent(context,HeadPortraitActivity.class);
                intent_head.putExtra("account",account);
                startActivityForResult(intent_head,MallConstant.USER_FRAGMENT_HEAD_PORTRAIT_REQUEST_CODE);
                Utils.actUseAnim(context,R.transition.explode,R.transition.fade);
                break;
            case R.id.ll_user_info:
                //资料
                refreshUserInfo();
                if(userInfos.size()!=0 && userInfos.get(0).getUserType()==1){
                    Intent intent_update = new Intent(getActivity(),UserSelfActivity.class);
                    startActivity(intent_update);
                    Utils.actUseAnim(context,R.transition.explode,R.transition.fade);
                    return;
                }
                Utils.tips(context,"请登录！");
                break;
            case R.id.ll_user_shopping_cart:
                //购物车
//                Utils.startActivity(context,ShoppingCartActivity.class);
                Intent intent_shopping = new Intent(context,ShoppingCartActivity.class);
                startActivity(intent_shopping);
                Utils.actUseAnim(getActivity(),R.transition.explode,R.transition.fade);
                break;
            case R.id.ll_user_order:
                //我的订单
                Utils.tips(context,"点击了订单！");
                break;
            case R.id.ll_user_address:
                //地址信息
                Intent intent_address = new Intent(context,AddressActivity.class);
                startActivity(intent_address);
                Utils.actUseAnim(getActivity(),R.transition.explode,R.transition.fade);
                break;
            case R.id.ll_user_agreement:
                //用户协议
                AgreementDialog dialog = new AgreementDialog(context,R.style.DialogTheme);
                //点击布局外，隐藏dialog
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                break;
            case R.id.ll_user_check:
                //检查更新
                if(!NetWorkUtil.getInstance().isNetworkConnected()){
                    Utils.tips(context,"网络异常");
                    return;
                }
                 CheckUpdateUtil.getInstance().checkUpdate(context,MallConstant.USER_FRAGMENT_ACTIVITY_ID);
                break;
            case R.id.ll_exception_test:
                //异常模拟
                CommonDialog exceptionTest = new CommonDialog.Builder(context)
                        .setContentTxt("此功能会导致应用闪退：模拟程序出现未知异常时，进行异常信息抓取并上报给开发者，是否继续？")
                        .setContentTxtColor(getResources().getColor(R.color.colorAccent))
                        .setSureText("继续")
                        .setCancelText("不了")
                        .setChooseListener(new CommonDialog.ChooseListener() {
                            @Override
                            public void sure() {
                                Log.w(TAG, "点击了继续异常模拟 ");
                                throw new RuntimeException("自定义异常：模拟异常！");
                            }
                            @Override
                            public void cancel() {
                                Log.w(TAG, "点击了取消异常模拟 ");
                            }
                        })
                        .build();
                exceptionTest.show();
                break;
            case R.id.tv_tips_login_logout:
                if(!typeOflogin){
                    //注销状态，根据注册/登陆结果切换为登录状态
                    LogUtil.d(TAG,"点击了登陆");
                    if(userInfos.size()==0){
                        //没有用户数据先注册
                        Intent intent = new Intent(context,RegisterActivity.class);
//                        startActivityForResult(intent,MallConstant.USER_FRAGMENT_REGISTER_REQUEST_CODE,ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
                        startActivity(intent);
                    }else {
                        //有用户数据，但是没登陆，进行登录
                        //这里的文本显示处理 已在ActivityForResult中处理
                        Intent intent2 = new Intent(context,LoginActivity.class);
                        intent2.putExtra("activityId",MallConstant.USER_FRAGMENT_ACTIVITY_ID);
                        startActivity(intent2);
                    }
                }else {
                    //登陆状态，切换为注销状态，清空昵称，替换为默认头像
                    LogUtil.d(TAG,"点击了注销");

                    tv_nick_name.setText("");
                    userInfos.get(0).setUserType(0);    // 0 游客
                    userInfos.get(0).save();
                    typeOflogin = false;
                    //已经注销，修改文本显示登陆
                    tv_tips_login_logout.setText(getResources().getString(R.string.user_login_des));
                    //使用默认图片
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.head_photo);
                    bitmap = ImageUtil.createCircleBitmap(bitmap);
                    iv_head_portrait.setImageBitmap(bitmap);
                }

                break;
        }
    }

    //刷新用户信息
    private void refreshUserInfo(){
        if(userInfos.size() != 0 ){
            userInfos.clear();
        }
        userInfos = DataSupport.findAll(UserInfo.class);
        if(userInfos.size()!=0){
            LogUtil.d(TAG,"cur_user : " + userInfos.get(0));
            account = userInfos.get(0).getName();
        }
    }

    public void loginRefresh(){
        //直接登陆场景走这步
        refreshUserInfo();
        LogUtil.d(TAG,"login success. userInfos:"+userInfos);
        //显示头像、昵称
        tv_nick_name.setText(userInfos.get(0).getNickName());
        //已经登录，文本显示修改为注销
        typeOflogin = true;
        //展示头像
        showHeadPhoto();
        tv_tips_login_logout.setText(getResources().getString(R.string.user_logout_des));
    }
}
