package com.cm.fm.mall.model.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.view.activity.CommodityActivity;
import com.cm.fm.mall.model.bean.ProductMsg;
import com.cm.fm.mall.model.bean.ShoppingProduct;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.ResourceUtils;
import com.cm.fm.mall.common.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewShoppingAdapter extends RecyclerView.Adapter<RecycleViewShoppingAdapter.MyViewHolder> {
    private List<ShoppingProduct> productLists;
    private Activity context;
    private MyViewHolder viewHolder;

    private final String TAG = "FM_RVShoppingAdapter";


    public RecycleViewShoppingAdapter(List<ShoppingProduct> productLists, Activity context) {
        this.productLists = productLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        //加载子view视图，创建viewholder
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_shopping_cart_recycview_item,viewGroup,false);
        viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int i) {
        //当前商品
        final ShoppingProduct product = productLists.get(i);
//        LogUtil.d(TAG,"product : " + product.toString());
        viewHolder.iv_shopping_product_image.setImageResource(ResourceUtils.getMipmapId(context,product.getExtension()));
        viewHolder.tv_shopping_product_name.setText(product.getProductName());
        viewHolder.tv_shopping_product_description.setText(product.getProductDescription());
        viewHolder.tv_shopping_buyNum.setText(String.valueOf(product.getBuyNum()));
        viewHolder.tv_shopping_product_price.setText(String.format("￥%s", product.getPrice()));

        int curBuyNum = Integer.parseInt(viewHolder.tv_shopping_buyNum.getText().toString());
        //点击事件监听
        viewHolder.iv_shopping_reduce.setOnClickListener(new PositionListener(product,curBuyNum));
        viewHolder.iv_shopping_add.setOnClickListener(new PositionListener(product,curBuyNum));
        viewHolder.bt_shopping_delete.setOnClickListener(new PositionListener(product,curBuyNum));
        viewHolder.rl_shopping_product.setOnClickListener(new PositionListener(product,curBuyNum));

    }

    @Override
    public int getItemCount() {
        return productLists.size();
    }

    public int getPosition(){
        return 1;
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        Button bt_shopping_delete;
        ImageView iv_shopping_product_image,iv_shopping_add,iv_shopping_reduce;
        TextView tv_shopping_product_name,tv_shopping_product_description,tv_shopping_product_price,tv_shopping_buyNum;
        RelativeLayout rl_shopping_product;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rl_shopping_product = itemView.findViewById(R.id.rl_shopping_product);
            bt_shopping_delete = itemView.findViewById(R.id.bt_shopping_delete);
            iv_shopping_reduce = itemView.findViewById(R.id.iv_shopping_reduce);
            iv_shopping_add = itemView.findViewById(R.id.iv_shopping_add);
            tv_shopping_buyNum = itemView.findViewById(R.id.tv_shopping_buyNum);
            iv_shopping_product_image = itemView.findViewById(R.id.iv_shopping_product_image);
            tv_shopping_product_name = itemView.findViewById(R.id.tv_shopping_product_name);
            tv_shopping_product_description = itemView.findViewById(R.id.tv_shopping_product_description);
            tv_shopping_product_price = itemView.findViewById(R.id.tv_shopping_product_price);

        }
    }

    class PositionListener implements View.OnClickListener {
        public ShoppingProduct product;
        public int curBuyNum;

        public PositionListener(ShoppingProduct product,int curBuyNum) {
            this.product = product;
            this.curBuyNum = curBuyNum;

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_shopping_reduce:
                    //点击减
                    LogUtil.d(TAG,"减-购买数量：" + curBuyNum);
                    curBuyNum--;
                    if (curBuyNum <= 1) {
                        curBuyNum = 1;
                    }
                    viewHolder.tv_shopping_buyNum.setText(String.valueOf(curBuyNum));
                    LogUtil.d(TAG,"减-购买数量修改后：" + curBuyNum);
                    saveNewBuyNum(curBuyNum, product.getId());
                    break;
                case R.id.iv_shopping_add:
                    //点击加
                    LogUtil.d(TAG,"加-购买数量：" + curBuyNum);
                    curBuyNum++;
                    viewHolder.tv_shopping_buyNum.setText(String.valueOf(curBuyNum));
                    LogUtil.d(TAG,"加-购买数量修改后：" + curBuyNum);
                    saveNewBuyNum(curBuyNum, product.getId());
                    break;
                case R.id.bt_shopping_delete:
                    //点击删除
                    LogUtil.d(TAG, "del_product:" + product.toString());
                    DataSupport.delete(ShoppingProduct.class, product.getId());
                    //更新adapter
                    update();
                    updateTotal();
                    break;
                case R.id.rl_shopping_product:
                    //点击item跳转
                    LogUtil.d(TAG, "cur_product:" + product.toString());
                    ProductMsg productMsg = new ProductMsg(product.getProductID(), product.getProductName(), product.getProductDescription(), product.getType(),
                            product.getPrice(), product.getInventory(), product.getExtension());
                    Utils.startActivityData(context, CommodityActivity.class, productMsg);
                    break;
            }
        }

    }

    public void update(){
        List<ShoppingProduct> products = DataSupport.findAll(ShoppingProduct.class);
        productLists.clear();
        productLists.addAll(products);
        notifyDataSetChanged();
    }

    public void saveNewBuyNum(int curBuyNum,int id){
        LogUtil.d(TAG,"saveNewBuyNum,curBuyNum:"+curBuyNum+",id:"+id);

        //并同步修改数据库中当前商品的购买数量
        ContentValues values = new ContentValues();
        values.put("buyNum",curBuyNum);
        DataSupport.update(ShoppingProduct.class,values, id);
        //上面数据有变化，所以更新list，并刷新适配器
        update();
        updateTotal();
    }

    //修改总金额数据
    public void updateTotal(){
        int total =0;
        if(productLists.size() != 0){
            for (ShoppingProduct shoppingProduct:productLists) {
                total += (shoppingProduct.getPrice() * shoppingProduct.getBuyNum());
            }
        } else {
            ImageView iv_tip_des = context.findViewById(R.id.iv_tip_des);
            RecyclerView rv_shopping_cart_list = context.findViewById(R.id.rv_shopping_cart_list);
            rv_shopping_cart_list.setVisibility(View.GONE);
            iv_tip_des.setVisibility(View.VISIBLE);
        }
        LogUtil.d(TAG,"updateTotal: " + total);
        //通过context设置显示总额的textview
        TextView tv_sum_money = context.findViewById(R.id.tv_sum_money);

        if (tv_sum_money != null) {
            tv_sum_money.setText("￥"+total);
        }
    }


}
