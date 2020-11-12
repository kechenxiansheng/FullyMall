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
import com.cm.fm.mall.util.CheckUpdateUtil;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.NetWorkUtil;

/**
 * 协议弹框
 */
public class AgreementDialog extends Dialog {
    Context context;
    TextView tv_msg;
    private final String tag = "TAG_AgreementDialog";
    public AgreementDialog(Context context,int style){
        super(context,style);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.layout_agreement_dialog,null);
        setContentView(view);
        //TODO 通过 Window 设置在整个窗口居中显示
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        tv_msg = view.findViewById(R.id.tv_msg);

        String bksw =
                "亲爱的用户：\n"+
                "    当您启动本app时，开发者‘晨明’便默认表示您已经同意‘晨明’满足了以下几点，并且是真心诚意的赞同。\n" +
                "    1、‘晨明’勤奋好学，在学习上永远是自己学习的主人。\n" +
                "    2、‘晨明’在社会中，如水滴融入大海的‘晨明’，是个闪着文明之光的新时代代表，带着希冀的翅膀向下期迈进。\n" +
                "    3、‘晨明’在工作上，上进心强，并具极丰富的人际关系技巧，能和身边的朋友打成一片。\n" +
                "    4、‘晨明’心地善良，和蔼可亲，乐于助人，乐善好施不可枚举，其优点多得有如滔滔江水连绵不绝，又如黄河泛滥一发而不可收拾，实乃人中之龙，旷世奇才也。\n" +
                "    5、‘晨明’英俊潇洒，帅呆酷毕，风流倜傥，玉树临风，一表人才，聪明伶俐，活泼可爱，谈吐大方，风度翩翩，气宇不凡，全国十大杰出优秀青年。\n" +
                "当然，‘晨明’如此的优秀，首先要感谢父母，老师和国家的辛勤栽培！\n";

        tv_msg.setText(bksw);
    }
}
