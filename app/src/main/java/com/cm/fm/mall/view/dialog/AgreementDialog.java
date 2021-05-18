package com.cm.fm.mall.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cm.fm.mall.R;

/**
 * 协议弹框
 */
public class AgreementDialog extends Dialog {
    Context context;
    TextView tv_msg;
    private final String TAG = "FM_AgreementDialog";
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
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
        }


        tv_msg = view.findViewById(R.id.tv_msg);

        String bksw =
                "亲爱的用户：\n"+
                "    1、很多时候，生活给你一个比别人低的起点，是为了让你上演一场绝地反击的故事。就像跳远，些许的退后，是为了跳得更远。每一次持续努力过后的成就，都是生活对不放弃的人最好的奖赏。\n" +
                "    2、一生很短，没必要对生活过于计较，有些事弄不懂，就不去懂；有些人猜不透，就不去猜；有些理儿想不通，就不去想。把不愉快的过往，在无人的角落，折叠收藏。告诉自己：可以不完美，但一定要真实；可以不富有，但一定要快乐。别为难自己，人生短短几十年，拼也好，闲也罢，都是瞬间。给自己一份乐观，给自己一份平和，保持最真的情怀，保持最好的心情。\n" +
                "    "
                ;
        tv_msg.setText(bksw);
    }
}
