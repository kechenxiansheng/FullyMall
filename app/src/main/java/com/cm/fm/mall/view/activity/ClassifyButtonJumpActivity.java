package com.cm.fm.mall.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseActivity;
import com.cm.fm.mall.model.adapter.ClassifyLeftMenuAdapter;
import com.cm.fm.mall.model.adapter.ClassifyRightDataAdapter;
import com.cm.fm.mall.model.bean.ClassifyCategory;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.Utils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 商城页 顶部分类按钮跳转页
 */
public class ClassifyButtonJumpActivity extends BaseActivity {
    private LinearLayout ll_mall_search;
    private ListView ll_classify_left_menu,ll_classify_right_data;         //左侧分类主条目菜单,右侧分条目数据
    private Activity context;
    private List<String> leftMenuList = new ArrayList<>();                      //左侧菜单集合
    private List<ClassifyCategory.DataBean> rightDataList = new ArrayList<>();  //右侧数据集合
    private List<Integer> showTitle = new ArrayList<>();                        //左侧分类的索引
    private int currentItem;            //当前的选项索引

    private String tag = "TAG_ClassifyButtonJumpActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_button);
        context = this;
        //初始化数据
        initData();

        ll_mall_search = findViewById(R.id.ll_mall_search);
        ll_classify_left_menu = findViewById(R.id.ll_classify_left_menu);
        ll_classify_right_data = findViewById(R.id.ll_classify_right_data);
        //左侧菜单适配器
        final ClassifyLeftMenuAdapter leftMenuAdapter = new ClassifyLeftMenuAdapter(context,leftMenuList);
        ll_classify_left_menu.setAdapter(leftMenuAdapter);
        //右侧数据适配器
        ClassifyRightDataAdapter rightDataAdapter = new ClassifyRightDataAdapter(context,rightDataList);
        ll_classify_right_data.setAdapter(rightDataAdapter);
        //点击左侧菜单，响应事件，并联动右侧数据
        ll_classify_left_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                leftMenuAdapter.setSelectItem(position);
                leftMenuAdapter.notifyDataSetInvalidated();
                //左侧菜单选中某项时，通知右侧listview定位数据位置（setSelection 设置当前选定的项）
                ll_classify_right_data.setSelection(showTitle.get(position));
            }
        });
        //右侧数据滚动监听
        ll_classify_right_data.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int scrollState;
            /**
             * 监听 ListView 的滑动状态改变。官方的有三种状态 SCROLL_STATE_TOUCH_SCROLL、SCROLL_STATE_FLING、SCROLL_STATE_IDLE：
             * SCROLL_STATE_TOUCH_SCROLL: 手指正拖着ListView滑动
             * SCROLL_STATE_FLING: ListView 正自由滑动
             * SCROLL_STATE_IDLE: ListView 滑动后静止
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //监听滑动状态
                this.scrollState = scrollState;
            }
            /**
             * firstVisibleItem：表示在屏幕中第一条显示的数据在adapter中的位置
             * visibleItemCount：则表示屏幕中最后一条数据在adapter中的位置
             * totalItemCount：在adapter中的总条数
             * */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    return;
                }
                LogUtil.d(tag,firstVisibleItem +","+visibleItemCount +","+totalItemCount);
                //右侧第一条数据 对应在左侧菜单分类的定位
                int current = showTitle.indexOf(firstVisibleItem);
                if (currentItem != current && current >= 0) {
                    //右侧数据滚动时，如果定位与左边选中的定位不匹配，则通知左边菜单同步定位至所属菜单item
                    currentItem = current;
                    leftMenuAdapter.setSelectItem(currentItem);
                    leftMenuAdapter.notifyDataSetInvalidated();
                }
            }
        });
        ll_mall_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转至搜索页面
//                Intent intent = new Intent(context,SearchActivity.class);
//                intent.putExtra("activityId",PERMISSION_REQUEST_CODE);
//                startActivityForResult(intent,101);
                Utils.getInstance().tips(context,"本页搜索功能未添加");
            }
        });
    }

    public void initData() {
        String json = getJson(context,"category.json");
        //拿到初级解析结果（code，data）
        ClassifyCategory classifyCategory = new Gson().fromJson(json, ClassifyCategory.class);

        for (int i = 0; i < classifyCategory.getData().size(); i++) {
            //获取分类主条目数据（type，dataList）
            ClassifyCategory.DataBean dataBean = classifyCategory.getData().get(i);
            //将type 放入左侧分类集合中
            leftMenuList.add(dataBean.getType());
            //存储左侧分类选项的索引
            showTitle.add(i);
            rightDataList.add(dataBean);
        }
        LogUtil.d(tag,"left:"+leftMenuList +",right:"+rightDataList +",title:"+showTitle);

    }
    //解析json文件中的数据
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(fileName), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
