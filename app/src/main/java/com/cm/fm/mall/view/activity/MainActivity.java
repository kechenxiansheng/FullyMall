package com.cm.fm.mall.view.activity;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseActivity;
import com.cm.fm.mall.common.util.CheckUpdateUtil;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.NetWorkUtil;
import com.cm.fm.mall.common.util.Utils;
import com.cm.fm.mall.view.fragment.menu.ClassifyFragment;
import com.cm.fm.mall.view.fragment.menu.FoundFragment;
import com.cm.fm.mall.view.fragment.menu.MallFragment;
import com.cm.fm.mall.view.fragment.menu.UserFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * 主页
 * 顶部是搜索栏
 * 底部是菜单栏（商城，分类，发现）
 */
public class MainActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    public MainActivity context;

    MallFragment mallFragment;
    ClassifyFragment classifyFragment;
    FoundFragment foundFragment;
    UserFragment userFragment;
    TabLayout tl_menu_bar;
    FrameLayout fl_data_center;

    private int pageId = -1;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private TabLayout.Tab tab;

    String[] titles = {"商城","分类","发现","我的"};
    int[] menuIcon = {R.mipmap.menubar_mall,R.mipmap.menubar_classify,R.mipmap.menubar_explore,R.mipmap.menubar_user};

    private final String TAG = "FM_MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate ");
        //防止软键盘吧布局向上顶的问题，setContentView 之前设置（突然无效了。。）
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED);
        setContentView(R.layout.activity_main);
        context = this;
        initView();
        if(savedInstanceState != null){
            int fragmentId = savedInstanceState.getInt("fragmentId");
            LogUtil.d(TAG, "fragmentId: "+fragmentId);
            //直接显示用户页面
            if(fragmentId ==3 && userFragment!=null){
                userFragment.loginRefresh();
                showFragment(fragmentId);
            }
        }else {
            //默认展示商城
            showFragment(0);
        }
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
        //点击监听
        tl_menu_bar.addOnTabSelectedListener(this);
        appUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG,"onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG,"onResume ");
        if(pageId == 3){
            if(userFragment!=null){
                userFragment.loginRefresh();
                showFragment(pageId);
            }
        }
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.d(TAG,"onRestart");
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
        pageId = tab.getPosition();
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
                    LogUtil.d(TAG,"show mallFragment");
                    transaction.show(mallFragment);
                }else {
                    mallFragment = new MallFragment();
                    transaction.add(R.id.fl_data_center, mallFragment);
                }
                break;
            case 1:
                if (classifyFragment != null) {
                    LogUtil.d(TAG,"show classifyFragment");
                    transaction.show(classifyFragment);
                }else {
                    classifyFragment = new ClassifyFragment();
                    transaction.add(R.id.fl_data_center, classifyFragment);
                }
                break;
            case 2:
                if (foundFragment != null) {
                    LogUtil.d(TAG,"show foundFragment");
                    transaction.show(foundFragment);
                }else {
                    foundFragment = new FoundFragment();
                    transaction.add(R.id.fl_data_center, foundFragment);
                }
                break;
            case 3:
                if (userFragment != null) {
                    LogUtil.d(TAG,"show userFragment");
                    transaction.show(userFragment);
                }else {
                    userFragment = new UserFragment();
                    transaction.add(R.id.fl_data_center, userFragment);
                }
                break;
        }
        transaction.commitNow();
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
            LogUtil.e(TAG, "exit");
            this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

}
