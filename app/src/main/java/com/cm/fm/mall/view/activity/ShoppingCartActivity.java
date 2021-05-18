package com.cm.fm.mall.view.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseMVPActivity;
import com.cm.fm.mall.contract.activity.ShoppingCartContract;
import com.cm.fm.mall.model.adapter.RecycleViewShoppingAdapter;
import com.cm.fm.mall.model.bean.ShoppingProduct;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.presenter.activity.ShoppingCartPresenter;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingCartActivity extends BaseMVPActivity<ShoppingCartPresenter> implements ShoppingCartContract.View,View.OnClickListener {
    public static Activity context;
    private TextView tv_settlement,tv_sum_money;
    private ImageView iv_tip_des;
    private RecyclerView rv_shopping_cart_list;
    private RecycleViewShoppingAdapter shoppingAdapter;
    private final String TAG = "FM_ShoppingCart";

    private List<ShoppingProduct> products = new ArrayList<>();
    @Override
    protected int initLayout() {
        context = this;
        return R.layout.activity_shopping_cart_rv;
    }

    @Override
    protected void initView() {
        tv_sum_money = findViewById(R.id.tv_sum_money);
        tv_settlement = findViewById(R.id.tv_settlement);
        iv_tip_des = findViewById(R.id.iv_tip_des);
        tv_settlement.setOnClickListener(this);
        rv_shopping_cart_list = findViewById(R.id.rv_shopping_cart_list);
    }

    @Override
    protected void initDataEnd() {
        super.initDataEnd();
        //查询所有已加入购物车的数据
        products = mPresenter.queryAllShoppingCartProducts();
        //加入购物车的数据为空的，则不处理
        if(products.size() == 0){
            //如果没有数据，购物车界面展示图片
            rv_shopping_cart_list.setVisibility(View.GONE);
            iv_tip_des.setVisibility(View.VISIBLE);
            return;
        }
        //计算总价格
        double total = mPresenter.calculateTotals(products);
        LogUtil.d(TAG,"queryTotals: " + total);
        tv_sum_money.setText("￥"+total);
        shoppingAdapter = new RecycleViewShoppingAdapter(products,this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        rv_shopping_cart_list.setLayoutManager(manager);
        rv_shopping_cart_list.setAdapter(shoppingAdapter);
        //给RecycleView item设置分割线（divider：分割物）
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.my_divider);
        if (drawable != null) {
            itemDecoration.setDrawable(drawable);
        }
        rv_shopping_cart_list.addItemDecoration(itemDecoration);
    }

    @Override
    protected ShoppingCartPresenter createPresenter() {
        return new ShoppingCartPresenter();
    }

    @Override
    public void OnLoginCheckResult(int code, String msg) {
        LogUtil.d(TAG,"OnLoginCheckResult code : " + code + ",msg : "+ msg);
        switch (code){
            case -1:
                //说明是首次使用app，去注册
                Utils.startActivity(this,RegisterActivity.class);
                break;
            case 1:
                //游客状态去登陆
                Utils.startActivity(this,LoginActivity.class,MallConstant.SHOPPING_CART_ACTIVITY_ACTIVITY_ID);
                break;
            case 0:
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
//                    return;
                }
//                Utils.tips(context,"提示：购买成功！");
                Utils.sendNotification(context,"订单发货提醒","FullyMall","您的订单已发货！",400);
                break;
        }
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
    public void finish() {
        super.finish();
        Utils.actUseAnim(this,R.transition.explode,R.transition.fade);
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
                    Utils.tips(context,"购物车空空如也！");
                    return;
                }
                //检验用户是否注册/登陆，在根据回调结果做结算处理
                mPresenter.checkLoginP();
                break;
        }
    }

}
