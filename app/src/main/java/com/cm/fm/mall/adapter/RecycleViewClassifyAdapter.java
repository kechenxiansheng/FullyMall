//package com.cm.fm.mall.adapter;
//
//import android.app.Activity;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.cm.fm.mall.R;
//import com.cm.fm.mall.activity.ProductActivity;
//import com.cm.fm.mall.bean.ProductMsg;
//import com.cm.fm.mall.util.ResourceUtils;
//import com.cm.fm.mall.util.Utils;
//
//import java.util.List;
//
//public class RecycleViewClassifyAdapter extends RecyclerView.Adapter<RecycleViewClassifyAdapter.MyViewHolder> {
//    private List<ProductMsg> productLists;
//    private Activity context;
//
//
//    public RecycleViewClassifyAdapter(List<ProductMsg> productLists, Activity context) {
//        this.productLists = productLists;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        //加载子view视图，创建viewholder
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recycleview_item,viewGroup,false);
//        final MyViewHolder viewHolder = new MyViewHolder(view);
//
//        viewHolder.ll_product.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO 获取到具体商品后跳转到商品详情界面
//                int position =  viewHolder.getLayoutPosition(); //获取当前item的id
//                ProductMsg product = productLists.get(position);
//                Utils.getInstance().startActivityData(context,ProductActivity.class,product);
////                Utils.getInstance().tips(context,"点击了："+ product.getProductName());
//            }
//        });
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
//        ProductMsg product = productLists.get(i);
//        int imageId = ResourceUtils.getMipmapId(context,product.getExtension());
//        //初始化子item视图
//        viewHolder.iv_product_image.setImageResource(imageId);
//        viewHolder.tv_product_description.setText(product.getProductDescription());
//
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return productLists.size();
//    }
//
//
//    class MyViewHolder extends RecyclerView.ViewHolder{
//        LinearLayout ll_product;
//        ImageView iv_product_image;
//        TextView tv_product_description;
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            ll_product = itemView.findViewById(R.id.ll_product);
//            iv_product_image = itemView.findViewById(R.id.iv_product_image);
//            tv_product_description = itemView.findViewById(R.id.tv_product_description);
//        }
//
//    }
//}
