package com.cm.fm.mall.view.fragment.menu;

import android.app.Activity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.model.adapter.ClassifyLeftMenuAdapter;
import com.cm.fm.mall.model.adapter.ClassifyRightDataAdapter;
import com.cm.fm.mall.model.bean.ClassifyCategory;
import com.cm.fm.mall.base.BaseMVPFragment;
import com.cm.fm.mall.presenter.fragment.ClassifyPresenter;
import com.cm.fm.mall.common.util.LogUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类页面
 * 左侧主条目，右侧分条目及子条目
 */
public class ClassifyFragment extends BaseMVPFragment<ClassifyPresenter> implements View.OnClickListener {

    private ListView ll_classify_left_menu,ll_classify_right_data;         //左侧分类主条目菜单,右侧分条目数据
    private Activity context;
    private List<String> leftMenuList = new ArrayList<>();                      //左侧菜单集合
    private List<ClassifyCategory.DataBean> rightDataList = new ArrayList<>();  //右侧数据集合
    private List<Integer> showTitle = new ArrayList<>();                        //左侧分类的索引
    private int currentItem;            //当前的选项索引

    private String tag = "TAG_ClassifyFragment";

    @Override
    public int initLayout() {
        return R.layout.fragment_classify;
    }
    @Override
    protected ClassifyPresenter createPresenter() {
        return new ClassifyPresenter();
    }

    @Override
    protected void initDataFront() {
        super.initDataEnd();
        //初始化数据
        String json = mPresenter.getJsonData("category.json");
        LogUtil.d(tag,"json 解析结果：" + json);
        if(json.isEmpty()){
            return;
        }
        //Gson 拿到初级解析结果（code，data）
        Gson gson = new Gson();
        ClassifyCategory classifyCategory = gson.fromJson(json, ClassifyCategory.class);
        //fastjson混淆后解析list会解析失败
//        ClassifyCategory classifyCategory = JSONObject.parseObject(json,ClassifyCategory.class);

        LogUtil.d(tag,"classifyCategory : " + classifyCategory.toString());
        List<ClassifyCategory.DataBean> data = classifyCategory.getData();
        if(data != null){
            for (int i = 0; i < data.size(); i++) {
                //获取分类主条目数据（type，dataList）
                ClassifyCategory.DataBean dataBean = data.get(i);
                //将type 放入左侧分类集合中
                leftMenuList.add(dataBean.getType());
                //存储左侧分类选项的索引
                showTitle.add(i);
                rightDataList.add(dataBean);
            }
        }
        LogUtil.d(tag,"left:"+leftMenuList +",right:"+rightDataList +",title:"+showTitle);
    }
    @Override
    public void initView(View view) {
        context = getActivity();
        ll_classify_left_menu = view.findViewById(R.id.ll_classify_left_menu);
        ll_classify_right_data = view.findViewById(R.id.ll_classify_right_data);
    }

    @Override
    protected void initDataEnd() {
        super.initDataEnd();
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
    public void dataDestroy() {

    }


}
