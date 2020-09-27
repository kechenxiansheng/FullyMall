package com.cm.fm.mall.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.util.CheckUpdateUtil;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.ResourceUtils;

/**
 * TODO 暂时用不到了，删除功能加载底薪详情页面了
 * 删除地址 确认弹框
 */
public class AddressDeleteDialog extends Dialog implements View.OnClickListener {
    Context context;
    TextView tv_mobile_network,tv_cancel_update,tv_sure_update;
    MyOnClickListener listener;
    private final String tag = "TAG_AddressDeleteDialog";
    public AddressDeleteDialog(Context context, int style,MyOnClickListener listener){
        super(context,style);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View tip_view = View.inflate(context,R.layout.layout_network_tip,null);
        setContentView(tip_view);
        //TODO 通过 Window 设置在整个窗口显示在中间
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        tv_mobile_network = tip_view.findViewById(R.id.tv_mobile_network);
        tv_cancel_update = tip_view.findViewById(R.id.tv_cancel_update);
        tv_sure_update = tip_view.findViewById(R.id.tv_sure_update);

        tv_mobile_network.setText(ResourceUtils.getStringId(context,"delete_address_tips"));
        tv_sure_update.setText(ResourceUtils.getStringId(context,"sure_text"));
        //取消删除
        tv_cancel_update.setOnClickListener(this);
        //确认删除
        tv_sure_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel_update:
                LogUtil.d(tag,"点击了取消");
                AddressDeleteDialog.this.dismiss();
                listener.cancel();
                break;
            case R.id.tv_sure_update:
                LogUtil.d(tag,"点击了继续");
                AddressDeleteDialog.this.dismiss();
                listener.sure();
                break;
        }
    }

    public interface MyOnClickListener{
        void sure();
        void cancel();
    }
}
