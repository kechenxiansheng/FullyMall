package com.cm.fm.mall.model.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.common.util.LogUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 地址 tag 适配器
 */
public class RecycleViewTagAdapter extends RecyclerView.Adapter<RecycleViewTagAdapter.MyViewHolder> {
    private List<String> tagList;
    private Activity context;
    private String choosed_tag_text = "";     //修改和增加都会传，增加默认空字符，修改没选择标签也是空字符
    private int choose_tag = -1;              //新选择的标签的id
    private String tag = "TAG_TagAdapter";
    private MyTagClickListener listener;

    public RecycleViewTagAdapter(List<String> list, Activity context) {
        this.tagList = list;
        this.context = context;
    }

    public void setChoose_tag(Integer choose_tag) {
        this.choose_tag = choose_tag;
        LogUtil.d(tag,"choose_tag : " + choose_tag);
    }

    public void setChoosed_tag_text(String choosed_tag_text) {
        this.choosed_tag_text = choosed_tag_text;
        LogUtil.d(tag,"choosed_tag_text : " + choosed_tag_text);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //加载子view视图，创建viewholder
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_tag_recycleview,viewGroup,false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        viewHolder.tv_tag_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position =  viewHolder.getLayoutPosition();
                //通知给listener，点击了哪一项
                listener.selected(position);
            }
        });
        return viewHolder;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        String tag_text = tagList.get(i);
        viewHolder.tv_tag_text.setText(tag_text);
        //如果是修改收货人时传过来的标签，需要修改与之匹配的item背景色，表示选中
        if(choose_tag == i){
            //选中标签
            viewHolder.tv_tag_text.setBackground(context.getDrawable(R.drawable.tag_select_bg));
        }else {
            //适配修改信息时传入的标签显示
            if(tag_text.equals(choosed_tag_text) ){
                viewHolder.tv_tag_text.setBackground(context.getDrawable(R.drawable.tag_select_bg));
                choosed_tag_text = "";
            }else {
                viewHolder.tv_tag_text.setBackground(context.getDrawable(R.drawable.tag_default_bg));
            }

        }
    }


    @Override
    public int getItemCount() {
        return tagList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_tag_text;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_tag_text = itemView.findViewById(R.id.tv_tag_text);
        }

    }
    public interface MyTagClickListener{
        void selected(int position);
    }

    public MyTagClickListener getListener() {
        return listener;
    }

    public void setListener(MyTagClickListener listener) {
        this.listener = listener;
    }
}
