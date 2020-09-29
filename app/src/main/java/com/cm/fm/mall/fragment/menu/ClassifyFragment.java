package com.cm.fm.mall.fragment.menu;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.cm.fm.mall.R;
import com.cm.fm.mall.adapter.ClassifyLeftMenuAdapter;
import com.cm.fm.mall.adapter.ClassifyRightDataAdapter;
import com.cm.fm.mall.adapter.ClassifyRightDataItemAdapter;
import com.cm.fm.mall.bean.ClassifyCategory;
import com.cm.fm.mall.fragment.BaseFragment;
import com.cm.fm.mall.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类页面
 * 左侧主条目，右侧分条目及子条目
 */
public class ClassifyFragment extends BaseFragment implements View.OnClickListener {

    private ListView ll_classify_left_menu,ll_classify_right_data;         //左侧分类主条目菜单,右侧分条目数据
    private Activity context;
    private List<String> leftMenuList = new ArrayList<>();                      //左侧菜单集合
    private List<ClassifyCategory.DataBean> rightDataList = new ArrayList<>();  //右侧数据集合
    private List<Integer> showTitle = new ArrayList<>();                        //左侧分类的索引
    private int currentItem;            //当前的选项索引

    private String tag = "TAG_ClassifyFragment";

    @Override
    public int getResource() {
        return R.layout.fragment_classify;
    }

    @Override
    public void init(View view) {
        context = getActivity();
        //初始化数据
        initData();

        ll_classify_left_menu = view.findViewById(R.id.ll_classify_left_menu);
        ll_classify_right_data = view.findViewById(R.id.ll_classify_right_data);
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
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void loadingData() {

    }

    @Override
    public void dataDestroy() {

    }

    public void initData() {
       String json = getJson(context,"category.json");
       //拿到初级解析结果（code，data）
        ClassifyCategory classifyCategory = JSONObject.parseObject(json,ClassifyCategory.class);

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
