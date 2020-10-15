package com.cm.fm.mall.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.adapter.AddressAdapter;
import com.cm.fm.mall.bean.AddressInfo;
import com.cm.fm.mall.dialog.AddressDeleteDialog;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.ResourceUtils;
import com.cm.fm.mall.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户所有的地址页面
 */
public class AddressActivity extends BaseActivity implements View.OnClickListener {
    private final int REQUEST_CODE = 700;
    public static final int REQUEST_CODE_EDIT = 701;
    ListView lv_user_address;
    ImageView address_back;
    LinearLayout ll_add_address;
    Activity context;
    AddressAdapter adapter;
    List<AddressInfo> list = new ArrayList<>();
    private String tag = "TAG_AddressActivity";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //告知页面，使用动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
        setContentView(R.layout.activity_address);

        address_back = findViewById(R.id.address_back);
        lv_user_address = findViewById(R.id.lv_user_address);
        ll_add_address = findViewById(R.id.ll_add_address);

        address_back.setOnClickListener(this);
        ll_add_address.setOnClickListener(this);

        initData();
        adapter = new AddressAdapter(context,list);
        lv_user_address.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE:
            case REQUEST_CODE_EDIT:
                if(resultCode == RESULT_OK){
                    initData();
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
    private void initData(){
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
