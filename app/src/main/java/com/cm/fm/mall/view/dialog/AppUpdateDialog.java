package com.cm.fm.mall.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cm.fm.mall.BuildConfig;
import com.cm.fm.mall.R;
import com.cm.fm.mall.common.util.CheckUpdateUtil;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.NetWorkUtil;

/**
 * 更新弹框
 */
public class AppUpdateDialog extends Dialog implements View.OnClickListener {
    Context context;
    TextView tv_cur_version_name,tv_new_version_name,tv_update_content,tv_next_update,tv_now_update;
    String newVersionName;
    String updateContent;
    private final String tag = "TAG_UpdateTipDialog";
    public AppUpdateDialog(Context context,int style,String newVersionName,String updateContent){
        super(context,style);
        this.context = context;
        this.newVersionName = newVersionName;
        this.updateContent = updateContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.layout_update_tip_dialog,null);
        setContentView(view);
        //显示在底部
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            //设置listview的高宽
            WindowManager.LayoutParams params = window.getAttributes();
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }



        tv_cur_version_name = view.findViewById(R.id.tv_cur_version_name);
        tv_new_version_name = view.findViewById(R.id.tv_new_version_name);
        tv_update_content = view.findViewById(R.id.tv_update_content);
        tv_next_update = view.findViewById(R.id.tv_next_update);
        tv_now_update = view.findViewById(R.id.tv_now_update);

        //展示更新版本信息和内容
        tv_cur_version_name.setText(BuildConfig.VERSION_NAME);
        tv_new_version_name.setText(newVersionName);
        tv_update_content.setText(updateContent);
        tv_update_content.setMovementMethod(ScrollingMovementMethod.getInstance()); //让内容过多时可以滚动显示
        //下次再说
        tv_next_update.setOnClickListener(this);
        //现在更新
        tv_now_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_next_update:
                LogUtil.d(tag,"点击了下次再说");
                AppUpdateDialog.this.dismiss();
                break;
            case R.id.tv_now_update:
                LogUtil.d(tag,"点击了现在更新");
                AppUpdateDialog.this.dismiss();

                if(NetWorkUtil.getConnectedType() == 0){
                    //如果是移动网络，弹框提示
                    MobileNetworkDialog dialog = new MobileNetworkDialog(context,R.style.DialogTheme);
                    dialog.show();
                }else if(NetWorkUtil.getConnectedType() == 1){
                    //wifi 直接下载
                    CheckUpdateUtil.getInstance().downloadApk(context);
                }
                break;
        }
    }
}
