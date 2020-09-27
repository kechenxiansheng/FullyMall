package com.cm.fm.mall.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.activity.ProductActivity;
import com.cm.fm.mall.bean.ProductMsg;
import com.cm.fm.mall.fragment.MallFragment;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.ResourceUtils;
import com.cm.fm.mall.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商城页导航栏适配器
 * */
public class RecycleViewMallGuideAdapter extends RecyclerView.Adapter<RecycleViewMallGuideAdapter.MyViewHolder> {
    private List<String> titles;
    private Activity context;
    private int selectItem = 0;     //当前选中的item
    private GuideItemListener listener;
    private String tag = "TAG_MallGuideAdapter";

    public RecycleViewMallGuideAdapter(List<String> titles, Activity context,GuideItemListener listener) {
        this.titles = titles;
        this.context = context;
        this.listener = listener;
    }

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
        LogUtil.d(tag,"selectItem : "+selectItem);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//        LogUtil.d(tag,"onCreateViewHolder viewType : " + viewType);
        /** 加载子view视图，创建 viewholder */
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recycleview_item_guide,viewGroup,false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, int position) {
        //设置标题
        String title = titles.get(position);
        viewHolder.tv_guide_title.setText(title);
        /** selectItem 为0 时，表示默认选中首页  */
        if(selectItem == position){
            viewHolder.tv_guide_title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }else {
            viewHolder.tv_guide_title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//            viewHolder.tv_guide_title.setBackgroundColor(context.getResources().getColor(R.color.colorLightBlue8));
        }

        /** 点击导航栏标题，改变背景色，并展示对应商品 */
        viewHolder.tv_guide_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取当前item的id
                int position =  viewHolder.getLayoutPosition();
                LogUtil.d(tag,"cur_position : " + position);
                //通知给 MallFragment 当前点击了哪项
                listener.onClick(position);
                notifyDataSetChanged();
            }
        });

    }


    @Override
    public int getItemCount() {
        return titles.size();
    }




    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_guide_title;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_guide_title = itemView.findViewById(R.id.tv_guide_title);
            //获取屏幕高宽
            List<Integer> sizeList= Utils.getInstance().getSize(context);
            if(sizeList.size() != 0){
                int needWidth = 0;
                int width = sizeList.get(0);
                int height = sizeList.get(1);
                if( width < height ){
                    needWidth = width;
                }else {
                    needWidth = height;
                }
                //设置适配器 item宽度，只显示5个，并且按屏幕等比例显示
                tv_guide_title.setWidth(needWidth/5);
            }
        }
    }

    //item 点击监听器
    public interface GuideItemListener{
        void onClick(int position);

    }
}
