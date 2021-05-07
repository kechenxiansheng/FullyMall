package com.cm.fm.mall.view.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseActivity;
import com.cm.fm.mall.view.fragment.menu.ClassifyFragment;
import com.cm.fm.mall.view.fragment.menu.FoundFragment;
import com.cm.fm.mall.view.fragment.menu.MallFragment;
import com.cm.fm.mall.view.fragment.menu.UserFragment;
import com.cm.fm.mall.common.util.CheckUpdateUtil;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.NetWorkUtil;
import com.cm.fm.mall.common.util.Utils;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * 主页
 * 顶部是搜索栏
 * 底部是菜单栏（商城，分类，发现）
 */
public class MainActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    public static MainActivity context;

    MallFragment mallFragment;
    ClassifyFragment classifyFragment;
    FoundFragment foundFragment;
    UserFragment userFragment;
    TabLayout tl_menu_bar;
    FrameLayout fl_data_center;

    private FragmentManager fragmentManager;
    private TabLayout.Tab tab;

    String[] titles = {"商城","分类","发现","我的"};
    int[] menuIcon = {R.mipmap.bt_mall1,R.mipmap.bt_classify2,R.mipmap.bt_explore,R.mipmap.bt_user};

    private final String tag = "TAG_MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //防止软键盘吧布局向上顶的问题，setContentView 之前设置（突然无效了。。）
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED);
        setContentView(R.layout.activity_main);
        context = this;
        fragmentManager = getSupportFragmentManager();
        initView();

    }
    public void initView(){
        fl_data_center = findViewById(R.id.fl_data_center);       //内容布局
        tl_menu_bar = findViewById(R.id.tl_menu_bar);             //底部菜单
        tl_menu_bar.setSelectedTabIndicator(0);                   //隐藏 TabLayout 下划线
        /** TabLayout 模式 滑动（MODE_SCROLLABLE）和固定（MODE_FIXED） */
        tl_menu_bar.setTabMode(TabLayout.MODE_FIXED);
        tl_menu_bar.setTabGravity(TabLayout.GRAVITY_FILL);         //平分屏幕宽度展示
        //TabLayout 设置 item 图标和标题
        for(int i = 0;i<titles.length;i++){
            tab = tl_menu_bar.newTab();
            //Tab 使用自定义的布局
            tab.setCustomView(getTabItemView(titles[i],menuIcon[i]));
            /**
             * 默认第一个item选中
             */
            if(i == 0){
                //获取item父类视图
                View tabview = (View)tab.getCustomView().getParent();
                tabview.setBackgroundColor(getResources().getColor(R.color.colorLightBlue11));
            }
            tl_menu_bar.addTab(tab);
        }

        //默认展示商城
        showFragment(0);
        //点击监听
        tl_menu_bar.addOnTabSelectedListener(this);

        appUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(tag,"onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(tag,"onResume");
    }

    private void appUpdate(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //有网络，检查更新
                if(NetWorkUtil.getInstance().isNetworkConnected()){
                    CheckUpdateUtil.getInstance().checkUpdate(context,0);
                }
            }
        }).start();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //改变背景色
        View tabview = (View)tab.getCustomView().getParent();
        tabview.setBackgroundColor(getResources().getColor(R.color.colorLightBlue11));
        //展示选中的页卡fragment
        showFragment(tab.getPosition());
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View tabview = (View)tab.getCustomView().getParent();
        tabview.setBackgroundColor(getResources().getColor(R.color.colorLightBlue8));
    }
    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    //给自定义 item 设置icon和标题
    public View getTabItemView(String title,int icon){
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tablayout_item,null);
        TextView tv_title =  view.findViewById(R.id.tv_title);
        tv_title.setText(title);
        ImageView iv_image = view.findViewById(R.id.iv_image);
        iv_image.setImageResource(icon);
        return view;
    }
    //展示对应的fragment
    private void showFragment(int position) {
        //事务管理器
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //全部隐藏，根据传入的position展示对应的fragment
        hideFragment(transaction);
        switch (position) {
            case 0:
                if (mallFragment != null) {
                    LogUtil.d(tag,"show mallFragment");
                    transaction.show(mallFragment);
                }else {
                    mallFragment = new MallFragment();
                    transaction.add(R.id.fl_data_center, mallFragment);
                }
                break;
            case 1:
                if (classifyFragment != null) {
                    LogUtil.d(tag,"show classifyFragment");
                    transaction.show(classifyFragment);
                }else {
                    classifyFragment = new ClassifyFragment();
                    transaction.add(R.id.fl_data_center, classifyFragment);
                }
                break;
            case 2:
                if (foundFragment != null) {
                    LogUtil.d(tag,"show foundFragment");
                    transaction.show(foundFragment);
                }else {
                    foundFragment = new FoundFragment();
                    transaction.add(R.id.fl_data_center, foundFragment);
                }
                break;
            case 3:
                if (userFragment != null) {
                    LogUtil.d(tag,"show userFragment");
                    transaction.show(userFragment);
                }else {
                    userFragment = new UserFragment();
                    transaction.add(R.id.fl_data_center, userFragment);
                }
                break;
        }

        transaction.commit();     //需搭配 executePendingTransactions() 一起使用，来保证立即执行
        fragmentManager.executePendingTransactions();

//        transaction.commitAllowingStateLoss();
    }
    //隐藏fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (mallFragment != null) {
            transaction.hide(mallFragment);
        }
        if (classifyFragment != null) {
            transaction.hide(classifyFragment);
        }
        if (foundFragment != null) {
            transaction.hide(foundFragment);
        }
        if (userFragment != null) {
            transaction.hide(userFragment);
        }

    }

    private long clickTime=0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /**
         *  退出事件处理
         *  如果点击间隔时间大于两秒，提示再次点击退出
         *  两秒内两次点击则直接退出
         * */
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            //处理了此事件返回true（即不传递给上一级，事件分发机制，谁返回true谁处理）
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Utils.tips(context,"再次点击退出");
            clickTime = System.currentTimeMillis();
        } else {
            LogUtil.e(tag, "exit");
            this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

}
