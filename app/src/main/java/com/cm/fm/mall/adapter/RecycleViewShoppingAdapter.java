package com.cm.fm.mall.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.activity.CommodityActivity;
import com.cm.fm.mall.activity.ProductActivity;
import com.cm.fm.mall.activity.ShoppingCartActivity;
import com.cm.fm.mall.bean.ProductMsg;
import com.cm.fm.mall.bean.ShoppingProduct;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.ResourceUtils;
import com.cm.fm.mall.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.List;

public class RecycleViewShoppingAdapter extends RecyclerView.Adapter<RecycleViewShoppingAdapter.MyViewHolder> {
    private List<ShoppingProduct> productLists;
    private Activity context;
    private MyViewHolder viewHolder;
    private int curBuyNum;
    private final String tag = "TAG_RVShoppingAdapter";

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
//        LogUtil.d(tag,"product : " + product.toString());
        //初始化子item视图
        viewHolder.iv_shopping_product_image.setImageResource(ResourceUtils.getMipmapId(context,product.getExtension()));
        viewHolder.tv_shopping_product_name.setText(product.getProductName());
        viewHolder.tv_shopping_product_description.setText(product.getProductDescription());
        viewHolder.tv_shopping_buyNum.setText(Integer.valueOf(product.getBuyNum())+"");
        viewHolder.tv_shopping_product_price.setText("￥"+product.getPrice());


        //点击减
        viewHolder.iv_shopping_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //当前购买数量
                curBuyNum =  Integer.parseInt(viewHolder.tv_shopping_buyNum.getText().toString());
                curBuyNum--;
                if(curBuyNum <= 1){
                    curBuyNum = 1;
                }
                viewHolder.tv_shopping_buyNum.setText(String.valueOf(curBuyNum));
                saveNewBuyNum(curBuyNum,product.getId());
            }
        });
        //点击加
        viewHolder.iv_shopping_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curBuyNum =  Integer.parseInt(viewHolder.tv_shopping_buyNum.getText().toString());
                curBuyNum++;
                viewHolder.tv_shopping_buyNum.setText(String.valueOf(curBuyNum));
                saveNewBuyNum(curBuyNum,product.getId());
            }
        });
        //删除选择的商品
        viewHolder.bt_shopping_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击删除
                LogUtil.d(tag,"删除的商品信息 product:"+product.toString());
                DataSupport.delete(ShoppingProduct.class,product.getId());
                //更新adapter
                update();
                updateTotal();
            }
        });
        //点击商品item跳转至商品页面
        viewHolder.rl_shopping_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d(tag,"跳转至商品页面，当前 product 信息:"+product.toString());
                ProductMsg productMsg = new ProductMsg(product.getProductID(),product.getProductName(),product.getProductDescription(),product.getType(),
                        product.getPrice(),product.getInventory(),product.getExtension());
                Utils.getInstance().startActivityData(context,CommodityActivity.class,productMsg);
            }
        });


    }


    @Override
    public int getItemCount() {
        return productLists.size();
    }

//    @Override
//    public void onClick(View v) {
//        //当前购买数量
//        int curBuyNum =  Integer.parseInt(viewHolder.tv_shopping_buyNum.getText().toString());
//        //当前 item 索引
//        int position =  viewHolder.getAdapterPosition();
//        LogUtil.d(tag,"当前item position : " + position);
//        LogUtil.d(tag,"当前productLists 数量 : " + productLists.size());
//        ShoppingProduct product = productLists.get(position);
//        LogUtil.d(tag,"当前product : " + product.toString());
//        switch (v.getId()){
//            case R.id.iv_shopping_reduce:
//                //点击减
//                curBuyNum--;
//                if(curBuyNum <= 1){
//                    curBuyNum = 1;
//                }
//                viewHolder.tv_shopping_buyNum.setText(String.valueOf(curBuyNum));
//                saveNewBuyNum(curBuyNum,product.getId());
//                break;
//            case R.id.iv_shopping_add:
//                //点击加
//                curBuyNum++;
//                viewHolder.tv_shopping_buyNum.setText(String.valueOf(curBuyNum));
//                saveNewBuyNum(curBuyNum,product.getId());
//                break;
//            case R.id.bt_shopping_delete:
//                //点击删除
//                LogUtil.d(tag,"del_product:"+product.toString());
//                DataSupport.delete(ShoppingProduct.class,product.getId());
//                //更新adapter
//                update();
//                updateTotal();
//                break;
//            case R.id.rl_shopping_product:
//                //点击item跳转
//                LogUtil.d(tag,"cur_product:"+product.toString());
//                ProductMsg productMsg = new ProductMsg(product.getProductID(),product.getProductName(),product.getProductDescription(),product.getType(),
//                        product.getPrice(),product.getInventory(),product.getExtension());
//                Utils.getInstance().startActivityData(context,CommodityActivity.class,productMsg);
//                break;
//        }
//    }

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

    public void update(){
        List<ShoppingProduct> products = DataSupport.findAll(ShoppingProduct.class);
        productLists.clear();
        productLists.addAll(products);
        notifyDataSetChanged();
    }

    public void saveNewBuyNum(int curBuyNum,int id){
        LogUtil.d(tag,"saveNewBuyNum,curBuyNum:"+curBuyNum+",id:"+id);

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
        LogUtil.d(tag,"updateTotal: " + total);
        //通过context设置显示总额的textview
        TextView tv_sum_money = context.findViewById(R.id.tv_sum_money);

        if (tv_sum_money != null) {
            tv_sum_money.setText("￥"+total);
        }
    }


}
