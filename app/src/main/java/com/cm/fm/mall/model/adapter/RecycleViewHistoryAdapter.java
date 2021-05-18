package com.cm.fm.mall.model.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.model.bean.SearchHistory;

import org.litepal.crud.DataSupport;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 搜索历史记录适配器
 */
public class RecycleViewHistoryAdapter extends RecyclerView.Adapter<RecycleViewHistoryAdapter.MyViewHolder> {
    private List<SearchHistory> histories;
    private Activity context;
    private final String TAG = "FM_HistoryAdapter";


    public RecycleViewHistoryAdapter(List<SearchHistory> histories, Activity context) {
        this.histories = histories;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //加载子view视图，创建viewholder
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recycleview_item_history,viewGroup,false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.tv_history_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 点击哪个，就将哪个放入输入框
                int position =  viewHolder.getLayoutPosition(); //获取当前item的id
                SearchHistory history = histories.get(position);
                EditText et_search_msg = context.findViewById(R.id.et_search_msg);
                et_search_msg.setText(history.getMsg());

            }
        });
        viewHolder.ib_delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 点击删除
                int position =  viewHolder.getLayoutPosition(); //获取当前item的id
                SearchHistory history = histories.get(position);
                history.delete();
                histories = DataSupport.findAll(SearchHistory.class);
                notifyDataSetChanged();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        SearchHistory history = histories.get(i);
        //初始化子item视图
        viewHolder.tv_history_msg.setText(history.getMsg());

    }


    @Override
    public int getItemCount() {
//        LogUtil.d(TAG,"histories: " + histories.size());
        return histories.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageButton ib_delete_item;
        TextView tv_history_msg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ib_delete_item = itemView.findViewById(R.id.ib_delete_item);
            tv_history_msg = itemView.findViewById(R.id.tv_history_msg);
        }

    }
}
