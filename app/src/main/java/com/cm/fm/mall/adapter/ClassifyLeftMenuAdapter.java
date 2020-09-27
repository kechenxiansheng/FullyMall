package com.cm.fm.mall.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cm.fm.mall.R;

import java.util.List;

/**
 * 左侧菜单ListView的适配器
 */
public class ClassifyLeftMenuAdapter extends BaseAdapter {

    private Context context;
    private int selectItem = 0;
    private List<String> list;

    public ClassifyLeftMenuAdapter(Context context, List<String> list) {
        this.list = list;
        this.context = context;
    }

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
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
            view = View.inflate(context, R.layout.layout_item_menu_left, null);
            holder.tv_name = view.findViewById(R.id.item_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (position == selectItem) {
            //设置左侧分类菜单的背景色(选中时蓝色)
            holder.tv_name.setBackgroundColor(context.getResources().getColor(R.color.colorLightBlue11));
        } else {
            holder.tv_name.setBackgroundColor(context.getResources().getColor(R.color.colorLightBlue8));
        }
        holder.tv_name.setText(list.get(position));
        return view;
    }

    static class ViewHolder {
        private TextView tv_name;
    }
}
