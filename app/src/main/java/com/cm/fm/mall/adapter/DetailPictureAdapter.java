package com.cm.fm.mall.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cm.fm.mall.R;
import com.cm.fm.mall.activity.AddressActivity;
import com.cm.fm.mall.activity.AddressDetailActivity;
import com.cm.fm.mall.bean.AddressInfo;
import com.cm.fm.mall.util.Utils;

import java.util.List;

/**
 * 详情页面 图片 ListView 适配器
 */
public class DetailPictureAdapter extends BaseAdapter {

    private Activity context;
    private List<String> list;
    private String tag = "TAG_DetailPictureAdapter";

    public DetailPictureAdapter(Activity context, List<String> list) {
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        //初始化子布局
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_detail_picture_listview_item, null);
            holder = new ViewHolder();
            holder.iv_detail_picture = convertView.findViewById(R.id.iv_detail_picture);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //根据屏幕宽度设置图片大小
        List<Integer> size = Utils.getInstance().getSize(context);
        int width = size.get(0);
        ViewGroup.LayoutParams layoutParams = holder.iv_detail_picture.getLayoutParams();
        layoutParams.width = (int) (width*0.95);
        layoutParams.height = (int) (width*0.95);
        holder.iv_detail_picture.setLayoutParams(layoutParams);

        /** 使用 Glide 加载图片 */
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.loading)      //加载前的占位图
                .diskCacheStrategy(DiskCacheStrategy.NONE)  //禁用Glide的缓存功能
                .error(R.mipmap.error_bg);        //错误展示图
        String uri = list.get(position);
        Glide.with(context).load(uri).apply(options).into(holder.iv_detail_picture);
        return convertView;
    }

    private class ViewHolder {
        private ImageView iv_detail_picture;
    }
}
