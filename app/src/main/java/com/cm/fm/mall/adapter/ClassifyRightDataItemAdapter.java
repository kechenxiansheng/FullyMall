package com.cm.fm.mall.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cm.fm.mall.R;
import com.cm.fm.mall.model.bean.ClassifyCategory;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 右侧页面各分类的item适配器
 */
public class ClassifyRightDataItemAdapter extends BaseAdapter {

    private Context context;
    private List<ClassifyCategory.DataBean.DataListBean> dataListBeans;
    private String tag = "TAG_RightDataItemAdapter";
    public ClassifyRightDataItemAdapter(Context context, List<ClassifyCategory.DataBean.DataListBean> dataListBeans) {
        this.context = context;
        this.dataListBeans = dataListBeans;
    }


    @Override
    public int getCount() {
        LogUtil.d(tag,"dataListBeans:"+dataListBeans);
        if (dataListBeans != null) {
            return dataListBeans.size();
        } else {
            return 10;
        }
    }

    @Override
    public Object getItem(int position) {
        return dataListBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        LogUtil.d(tag,"position:"+position);
        //获取子类别信息
        final ClassifyCategory.DataBean.DataListBean dataListBean = dataListBeans.get(position);
//        LogUtil.d(tag,"dataListBean:"+dataListBean.toString());
        ViewHold viewHold = null;
        if (convertView == null) {

            convertView = View.inflate(context, R.layout.layout_item_right_data_category, null);
            viewHold = new ViewHold();
            viewHold.ll_item_title = convertView.findViewById(R.id.ll_item_title);
            viewHold.tv_child_title = convertView.findViewById(R.id.tv_child_title);
            viewHold.iv_item_album = convertView.findViewById(R.id.iv_item_album);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        viewHold.tv_child_title.setText(dataListBean.getName());
        /** 使用 Glide 加载图片 */
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.image_loading)      //加载前的占位图
//                .diskCacheStrategy(DiskCacheStrategy.NONE)  //禁用Glide的缓存功能
                .error(R.mipmap.error_bg);        //错误展示图
        String uri ="http://119.27.160.230:8080/image/icon/icon_dog.png";
        Glide.with(context).load(uri).apply(options).into(viewHold.iv_item_album);

        viewHold.ll_item_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.tips(context,"尚未配置此类商品："+dataListBean.getName());
            }
        });
        return convertView;
    }

    private static class ViewHold {
        private LinearLayout ll_item_title;         //父布局
        private TextView tv_child_title;            //标题
//        private SimpleDraweeView sdv_item_album;    //图片
        private ImageView iv_item_album;            //图片
    }

}
