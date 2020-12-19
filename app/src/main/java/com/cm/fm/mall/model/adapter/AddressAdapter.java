package com.cm.fm.mall.model.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.model.constant.MallConstant;
import com.cm.fm.mall.view.activity.AddressActivity;
import com.cm.fm.mall.view.activity.AddressDetailActivity;
import com.cm.fm.mall.model.bean.AddressInfo;
import com.cm.fm.mall.util.Utils;

import java.util.List;

/**
 * 地址页面 ListView 适配器
 */
public class AddressAdapter extends BaseAdapter {

    private Activity context;
    private List<AddressInfo> list;
    private String tag = "TAG_AddressAdapter";

    public AddressAdapter(Activity context, List<AddressInfo> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
//        LogUtil.d(tag,"地址数量：" + list.size());
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;

        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.layout_address_listview, null);

            holder.tv_user_name = view.findViewById(R.id.tv_user_name);
            holder.tv_user_phone = view.findViewById(R.id.tv_user_phone);
            holder.tv_address_cur_tag = view.findViewById(R.id.tv_address_cur_tag);
            holder.tv_address_detail = view.findViewById(R.id.tv_address_detail);
            holder.iv_edit_address = view.findViewById(R.id.iv_edit_address);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final AddressInfo addressInfo = list.get(position);

//        LogUtil.d(tag,"当前地址 : " + addressInfo.toString());
        holder.tv_user_name.setText(addressInfo.getUsername());
        //隐藏手机号的中间4位
        String phoneNum = addressInfo.getPhone();
        if(phoneNum.length() == 11){
            phoneNum = phoneNum.substring(0,3)+"****"+phoneNum.substring(7,phoneNum.length());
        }
        holder.tv_user_phone.setText(phoneNum);
        holder.tv_address_detail.setText(String.format("%s%s", addressInfo.getAddress(), addressInfo.getStreet()));

        //设置默认地址
        if(addressInfo.getTag() == null || addressInfo.getTag().isEmpty()){
            //没有标签记录 直接隐藏
            holder.tv_address_cur_tag.setVisibility(View.INVISIBLE);
        }else {
            //标签 内容
            holder.tv_address_cur_tag.setVisibility(View.VISIBLE);
            holder.tv_address_cur_tag.setText(addressInfo.getTag());
            //是否是默认地址
            if(addressInfo.isDefault()){
                holder.tv_address_cur_tag.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                holder.tv_address_cur_tag.setText("默认");
            }else {
                holder.tv_address_cur_tag.setBackgroundColor(context.getResources().getColor(R.color.colorLightBlue10));
            }
        }

        /** 点击编辑按钮 */
        holder.iv_edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().startActivityDataForResult(context,AddressDetailActivity.class,MallConstant.ADDRESS_ACTIVITY_REQUEST_CODE_EDIT,addressInfo);
            }
        });
        return view;
    }

    private class ViewHolder {
        private TextView tv_user_name,tv_user_phone,tv_address_cur_tag,tv_address_detail;
        private ImageView iv_edit_address;
    }
}
