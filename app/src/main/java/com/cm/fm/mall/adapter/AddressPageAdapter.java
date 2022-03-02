package com.cm.fm.mall.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.model.bean.AddressInfo;

import java.util.List;

/**
 * 底部弹框的地址页面 ListView 适配器
 */
public class AddressPageAdapter extends BaseAdapter {

    private Activity context;
    private List<AddressInfo> list;
    private AddressInfo choosedInfo;
    private String tag = "TAG_AddressPageAdapter";

    public AddressPageAdapter(Activity context,AddressInfo info, List<AddressInfo> list) {
        this.list = list;
        this.choosedInfo = info;
        this.context = context;
    }


    @Override
    public int getCount() {
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
            view = View.inflate(context, R.layout.layout_address_page_listview, null);

            holder.tv_user_name_bottom = view.findViewById(R.id.tv_user_name_bottom);
            holder.tv_address_detail_bottom = view.findViewById(R.id.tv_address_detail_bottom);
            holder.iv_address_choose = view.findViewById(R.id.iv_address_choose);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final AddressInfo addressInfo = list.get(position);

//        LogUtil.d(tag,"cur_addressInfo: " + addressInfo.toString());
//        LogUtil.d(tag,"choosedInfo: " + (choosedInfo==null));
        holder.tv_user_name_bottom.setText(addressInfo.getUsername());
        holder.tv_address_detail_bottom.setText(addressInfo.getAddress()+addressInfo.getStreet());
        if(choosedInfo!=null){
            if(choosedInfo.getId() == addressInfo.getId()){
                holder.iv_address_choose.setBackground(context.getDrawable(R.mipmap.address_icon1));
            }else {
                holder.iv_address_choose.setBackground(context.getDrawable(R.mipmap.address_icon2));
            }
        }else if(addressInfo.isDefault()){
            holder.iv_address_choose.setBackground(context.getDrawable(R.mipmap.address_icon1));
        }else {
            holder.iv_address_choose.setBackground(context.getDrawable(R.mipmap.address_icon2));
        }
        return view;
    }

    private class ViewHolder {
        private TextView tv_user_name_bottom,tv_address_detail_bottom;
        private ImageView iv_address_choose;
    }
}
