package com.cm.fm.mall.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseMVPActivity;
import com.cm.fm.mall.contract.activity.AddressContract;
import com.cm.fm.mall.model.adapter.AddressAdapter;
import com.cm.fm.mall.model.bean.AddressInfo;
import com.cm.fm.mall.presenter.activity.AddressPresenter;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends BaseMVPActivity<AddressPresenter> implements AddressContract.View,View.OnClickListener {
    private final int REQUEST_CODE = 700;
    public static final int REQUEST_CODE_EDIT = 701;
    ListView lv_user_address;
    ImageView address_back;
    LinearLayout ll_add_address;
    Activity context;
    AddressAdapter adapter;
    List<AddressInfo> list = new ArrayList<>();
    private String tag = "TAG_AddressActivity";

    @Override
    protected void activityAnim() {
        //告知页面，使用动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
    }

    @Override
    protected void initDataFront() {
        initList();
    }

    @Override
    protected AddressPresenter createPresenter() {
        return new AddressPresenter();
    }

    @Override
    protected void initView() {
        address_back = findViewById(R.id.address_back);
        lv_user_address = findViewById(R.id.lv_user_address);
        ll_add_address = findViewById(R.id.ll_add_address);

        address_back.setOnClickListener(this);
        ll_add_address.setOnClickListener(this);

        adapter = new AddressAdapter(context,list);
        lv_user_address.setAdapter(adapter);
    }

    @Override
    protected int initLayout() {
        context = this;
        return R.layout.activity_address;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE:
            case REQUEST_CODE_EDIT:
                if(resultCode == RESULT_OK){
                    initList();
                    adapter.notifyDataSetChanged();
                }
                break;
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //物理返回键关闭本页，也需要回传数据
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_OK);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_add_address:
                //新加地址
                Utils.getInstance().startActivityForResultAnimation(context,AddressDetailActivity.class,REQUEST_CODE);
                break;
            case R.id.address_back:
                context.finish();
                setResult(RESULT_OK);
                break;
        }
    }
    //初始化数据
    private void initList(){
        list.clear();
        List<AddressInfo> data = DataSupport.findAll(AddressInfo.class);
        if(data.size()>0){
            //将数据倒序存入list
            for(int i = data.size()-1; i>=0; i--){
                list.add(data.get(i));
            }
        }
        LogUtil.d(tag,"addressList size : " + list.size());
    }
}
