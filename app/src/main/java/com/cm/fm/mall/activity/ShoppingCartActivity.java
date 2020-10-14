package com.cm.fm.mall.activity;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.adapter.RecycleViewShoppingAdapter;
import com.cm.fm.mall.bean.ShoppingProduct;
import com.cm.fm.mall.bean.UserInfo;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.Utils;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车界面
 *  1、activityId : 4
 *  2、本页所有请求码以 400 开始

 */
public class ShoppingCartActivity extends BaseActivity implements View.OnClickListener {

    public static ShoppingCartActivity context;
    TextView tv_settlement,tv_sum_money;
    ImageView iv_tip_des;
    RecyclerView rv_shopping_cart_list;
    RecycleViewShoppingAdapter shoppingAdapter;
    public static final int SHOPPING_CART_ACTIVITY_ID = 4;
    private final String tag = "TAG-ShoppingCart";

    List<ShoppingProduct> products = new ArrayList<>();
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去掉Activity上面的状态栏 (注意这句话放的位置一定要放在setContentView之前)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_shopping_cart_rv);
        context = this;
        tv_sum_money = findViewById(R.id.tv_sum_money);
        tv_settlement = findViewById(R.id.tv_settlement);
        iv_tip_des = findViewById(R.id.iv_tip_des);
        tv_settlement.setOnClickListener(this);
        rv_shopping_cart_list = findViewById(R.id.rv_shopping_cart_list);
        queryTotals();
        if(products.size()==0){
            return;
        }
        shoppingAdapter = new RecycleViewShoppingAdapter(products,this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_shopping_cart_list.setLayoutManager(manager);
        rv_shopping_cart_list.setAdapter(shoppingAdapter);
        //给RecycleView item设置分割线
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.my_divider);
        if (drawable != null) {
            itemDecoration.setDrawable(drawable);
        }
        rv_shopping_cart_list.addItemDecoration(itemDecoration);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //物理返回键关闭本页，也需要回传数据
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_OK);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_settlement:

                Double sum_money = Double.parseDouble(tv_sum_money.getText().toString().replace("￥",""));
                if(sum_money == 0){
                    Utils.getInstance().tips(ShoppingCartActivity.this,"购物车空空如也！");
                    return;
                }
                checkUserLogin();
                break;
        }
    }
    //计算保存的所有购买商品总额
    public void queryTotals(){
        products = DataSupport.findAll(ShoppingProduct.class);
        LogUtil.d(tag,"queryTotals1: "+products.size());
        Double total = 0.0;
        if(products.size() != 0){
            for (ShoppingProduct shoppingProduct:products) {
                total += (shoppingProduct.getPrice() * shoppingProduct.getBuyNum());
            }
        }else {
            //如果没有数据，购物车界面展示图片
            rv_shopping_cart_list.setVisibility(View.GONE);
            iv_tip_des.setVisibility(View.VISIBLE);
        }
        LogUtil.d(tag,"queryTotals2: " + total);
        tv_sum_money.setText("￥"+total);
        LogUtil.d(tag,"queryTotals3");
    }
    //检验用户是否注册/登陆
    public void checkUserLogin(){
        List<UserInfo> userInfos = DataSupport.findAll(UserInfo.class);
        if(userInfos.size()==0){
            //说明是首次使用app，去注册
            Utils.getInstance().startActivity(this,RegisterActivity.class);
        }else {
            int userType = userInfos.get(0).getUserType();
            if(userType == 0){
                //游客状态去登陆
                Utils.getInstance().startActivity(this,LoginActivity.class,SHOPPING_CART_ACTIVITY_ID);
            }else {
                //购买成功,删除购物车数据
                for (ShoppingProduct product:products) {
                    DataSupport.delete(ShoppingProduct.class,product.getId());
                }
                //清空list
                products.clear();
                shoppingAdapter.notifyDataSetChanged();
                if(products.size()==0){
                    //没有数据了，展示图片，清空总金额
                    rv_shopping_cart_list.setVisibility(View.GONE);
                    iv_tip_des.setVisibility(View.VISIBLE);
                    tv_sum_money.setText("￥0.0");
                    return;
                }
                Utils.getInstance().tips(context,"提示：购买成功！");
                Utils.getInstance().sendNotification(context,"buy_suc","FullyMall","您的订单已发货！",400);
            }
        }
    }

}
