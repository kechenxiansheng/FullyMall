package com.cm.fm.mall.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.activity.AddressActivity;
import com.cm.fm.mall.adapter.AddressPageAdapter;
import com.cm.fm.mall.bean.AddressInfo;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 配送地址的底部弹窗页面
 */
public class AddressPageDialog extends Dialog implements View.OnClickListener {
    private Context context;
    TextView tv_other_address;
    ListView lv_address_page;
    AddressInfo choosedInfo;
    AddressPageAdapter pageAdapter;
    ImageView iv_address_page_close;
    List<AddressInfo> infoList = new ArrayList<>();
    ChooseListener listener;

    private String tag = "TAG_AddressPageDialog";

    public AddressPageDialog(@NonNull Context context,AddressInfo info,ChooseListener chooseListener) {
        super(context);
        this.context = context;
        this.choosedInfo = info;
        this.listener = chooseListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View tip_view = View.inflate(context,R.layout.activity_address_page,null);
        setContentView(tip_view);
        lv_address_page = tip_view.findViewById(R.id.lv_address_page);
        tv_other_address = tip_view.findViewById(R.id.tv_other_address);
        iv_address_page_close = tip_view.findViewById(R.id.iv_address_page_close);
        iv_address_page_close.setOnClickListener(this);
        tv_other_address.setOnClickListener(this);

        initData();
        //显示在底部
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(null);
        //设置listview的高宽
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);

        pageAdapter = new AddressPageAdapter((Activity) context,choosedInfo,infoList);
        lv_address_page.setAdapter(pageAdapter);
        pageAdapter.notifyDataSetChanged();
        //地址 listview 点击监听
        lv_address_page.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Utils.getInstance().tips(context,infoList.get(position).toString());
                AddressPageDialog.this.dismiss();
                listener.chooseResult(infoList.get(position));
            }
        });

    }

    private void initData(){
        infoList.clear();
        List<AddressInfo> list = DataSupport.findAll(AddressInfo.class);
        LogUtil.d(tag,"list size : " + list.size());
        if(list.size()>0){
            for (AddressInfo info: list) {
                if(info.isDefault()){
                    //将默认的地址信息索引移动到第一位
                    Collections.swap(list,list.indexOf(info),0);
                    break;
                }
            }
            infoList.addAll(list);
        }
        LogUtil.d(tag,"infoList size : " + infoList.size());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_address_page_close:
                //隐藏dialog
                AddressPageDialog.this.dismiss();
                break;
            case R.id.tv_other_address:
                //跳转到地址页
                Utils.getInstance().startActivity((Activity) context,AddressActivity.class);
                break;
        }
    }

    public interface ChooseListener{
        void chooseResult(AddressInfo info);
    }
}
