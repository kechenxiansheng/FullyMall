package com.cm.fm.mall.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseActivity;
import com.cm.fm.mall.model.adapter.RecycleViewHistoryAdapter;
import com.cm.fm.mall.model.bean.SearchHistory;
import com.cm.fm.mall.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchActivity extends BaseActivity implements View.OnClickListener {
    Activity context;
    RecyclerView rv_search_history;
    EditText et_search_msg;
    ImageButton ib_back,ib_delete_all,ib_search;

    //TODO 一定要保证 adapter 的 数据list 是同一个集合对象，否则刷新不生效。此时直接 new 一个list
    List<SearchHistory> histories = new ArrayList<>();
    RecycleViewHistoryAdapter historyAdapter;
    String tag = "TAG_SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;

        rv_search_history = findViewById(R.id.rv_search_history);
        et_search_msg = findViewById(R.id.et_search_msg);
        ib_back = findViewById(R.id.ib_back);
        ib_search = findViewById(R.id.ib_search);
        ib_delete_all = findViewById(R.id.ib_delete_all);   //删除所有历史记录
        //查询搜索历史记录
        histories = DataSupport.findAll(SearchHistory.class);

        LogUtil.d(tag,"histories size:" + histories.size());
        historyAdapter = new RecycleViewHistoryAdapter(histories,context);
        GridLayoutManager manager = new GridLayoutManager(context,2);  //网格布局，显示2列
        rv_search_history.setLayoutManager(manager);
        rv_search_history.setAdapter(historyAdapter);


        et_search_msg.setOnClickListener(this);
        ib_back.setOnClickListener(this);
        ib_search.setOnClickListener(this);
        ib_delete_all.setOnClickListener(this);

        //搜索框编辑事件监听（由于此事件不可插入换行符，所以需要在视图中设置singleLine属性,以及替换为 '搜索' 的 imeOptions）
        et_search_msg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                LogUtil.d(tag,"actionId:"+actionId + "event: " + (event==null));
                if(actionId == KeyEvent.KEYCODE_HOME){
                    //隐藏软键盘
                    ((InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    comesBack();
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.ib_search:
                //点击了搜索图片按钮
                comesBack();
                break;
            case R.id.ib_delete_all:
                LogUtil.d(tag,"删除所有历史记录");
                DataSupport.deleteAll(SearchHistory.class);
                histories.clear();
                LogUtil.d(tag,histories.hashCode()+"");
                //TODO 如果此时刷新不生效，看下 histories 还跟之前是不是同一个 list
                historyAdapter.notifyDataSetChanged();
                break;
        }
    }
    //回传数据
    public void comesBack(){
        String searchMsg = et_search_msg.getText().toString();
        //如果搜索内容不为空,并且没有相同内容，则缓存起来
        if(!TextUtils.isEmpty(searchMsg)){
            boolean boo = false;
            for (SearchHistory history:histories) {
                if(history.getMsg().equals(searchMsg.trim())){
                   boo = true;
                   break;
                }
            }
            //没有相同内容
            if(!boo){
                SearchHistory h = new SearchHistory();
                h.setMsg(searchMsg);
                h.save();
            }
        }
        //回传数据
        Intent intent = new Intent();
        intent.putExtra("search_msg",searchMsg);
        setResult(RESULT_OK,intent);
        SearchActivity.this.finish();
    }

}
