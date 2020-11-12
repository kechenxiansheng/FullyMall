package com.cm.fm.mall.model.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.model.bean.ClassifyCategory;
import com.cm.fm.mall.view.customview.GridViewForScrollView;
import com.cm.fm.mall.util.LogUtil;

import java.util.List;

/**
 * 右侧主界面ListView的适配器
 *
 * @author Administrator
 */
public class ClassifyRightDataAdapter extends BaseAdapter {

    private Context context;
    private List<ClassifyCategory.DataBean> dataBeans;  //type 和 datalist 层级
    private String tag = "TAG_RightDataAdapter";

    public ClassifyRightDataAdapter(Context context, List<ClassifyCategory.DataBean> dataBeans) {
        this.context = context;
        this.dataBeans = dataBeans;
    }

    @Override
    public int getCount() {
        LogUtil.d(tag,"dataBeans:"+dataBeans);
        if (dataBeans != null) {
            return dataBeans.size();
        } else {
            return 10;
        }
    }

    @Override
    public Object getItem(int position) {
        return dataBeans.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogUtil.d(tag,"position:"+position);
        //获取当前类别的信息（type，datalist）
        ClassifyCategory.DataBean dataBean = dataBeans.get(position);
        LogUtil.d(tag,"dataBean:"+dataBean.toString());
        //获取当前分类别（childType，childDatalists）信息
        List<ClassifyCategory.DataBean.DataListBean> dataListBeans = dataBean.getDatalist();
        LogUtil.d(tag,"dataListBeans:"+dataListBeans);
        ViewHold viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_item_right_data, null);
            viewHold = new ViewHold();
            viewHold.gv_grid_view = convertView.findViewById(R.id.gv_grid_view);
            viewHold.tv_blank =  convertView.findViewById(R.id.tv_blank);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        ClassifyRightDataItemAdapter adapter = new ClassifyRightDataItemAdapter(context, dataListBeans);
        viewHold.tv_blank.setText(dataBean.getType());  //设置 分类别的title（手机数码）
        viewHold.gv_grid_view.setAdapter(adapter);
        return convertView;
    }

    private static class ViewHold {
        private GridViewForScrollView gv_grid_view;
        private TextView tv_blank;
    }

}
