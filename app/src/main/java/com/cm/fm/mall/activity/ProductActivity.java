package com.cm.fm.mall.activity;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.adapter.ViewPagerAdapter;
import com.cm.fm.mall.bean.ProductMsg;
import com.cm.fm.mall.bean.ShoppingProduct;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.ResourceUtils;
import com.cm.fm.mall.util.Utils;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示商品详情的界面
 */
public class ProductActivity extends BaseActivity implements View.OnClickListener {

    private ProductActivity context;
    ViewPager vp_product;
    ImageView bt_reduce,bt_add;
    TextView tv_product_msg,tv_price,tv_inventory,tv_shopping_car,tv_image_tips,tv_buyNum;
    LinearLayout ll_go_to_shopping_car;
    List<View> views = new ArrayList<>();   //存放展示图片的list
    int[] images = {R.mipmap.p5,R.mipmap.p4,R.mipmap.p2,R.mipmap.p7};

    ProductMsg productMsg; //当前展示的商品

    private String tag ="TAG_ProductActivity";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context =this;
        //使用动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
        //去掉Activity上面的状态栏 (注意这句话放的位置一定要放在setContentView之前)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_product_info);

        tv_image_tips = findViewById(R.id.tv_image_tips);   //viewpager的当前item提示
        vp_product = findViewById(R.id.vp_product);         //展示商品图片的viewpager
        bt_reduce = findViewById(R.id.bt_reduce);           //减按钮
        bt_add = findViewById(R.id.bt_add);                 //加按钮
        tv_product_msg = findViewById(R.id.tv_product_msg); //商品描述
        tv_price = findViewById(R.id.tv_price);             //商品价格
        tv_inventory = findViewById(R.id.tv_inventory);     //库存数量
        tv_buyNum = findViewById(R.id.tv_buyNum);           //购买数量
        tv_shopping_car = findViewById(R.id.tv_shopping_car);   //加入购物车按钮
        ll_go_to_shopping_car = findViewById(R.id.ll_go_to_shopping_car);   //去购物车界面按钮

        bt_reduce.setOnClickListener(this);
        bt_add.setOnClickListener(this);
        tv_shopping_car.setOnClickListener(this);
        ll_go_to_shopping_car.setOnClickListener(this);

        //获取传过来的具体商品
        productMsg = getIntent().getParcelableExtra("product");
        if(productMsg != null){
            tv_product_msg.setText(productMsg.getProductDescription());
            tv_price.setText("￥"+productMsg.getPrice());
            tv_inventory.setText(String.valueOf(productMsg.getInventory()));
        }
        //商品信息初始化
        initPicture(productMsg);
        ViewPagerAdapter adapter = new ViewPagerAdapter(views);
        vp_product.setAdapter(adapter);
        //viewpager 页面滑动监测
        vp_product.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                /**
                 * 当前item滑动过程中会一直回调
                 * i 当前item的定位
                 * v 当前页面偏移的百分比
                 * i1 当前页面偏移的像素位置
                 */
            }
            @Override
            public void onPageSelected(int i) {
                //当前的item
                tv_image_tips.setText((i+1) + "/" + views.size());
            }
            @Override
            public void onPageScrollStateChanged(int i) {
                //此方法 在当前item状态改变的时候调用。i=1：表示正在滑动。 i=2：表示滑动结束。 i=0：表示啥都没做
            }
        });


        //TODO 获取手机的宽高
        List<Integer> sizeList= Utils.getInstance().getSize(context);
        if(sizeList.size() != 0){
            int needWidth = 0;
            int width = sizeList.get(0);
            int height = sizeList.get(1);
            if( width < height ){
                needWidth = (int) (width*0.6);
            }else {
                needWidth = (int) (height*0.6);
            }
            LogUtil.d(tag,"width:"+width+"  height:"+height+"  needWidth:"+needWidth);
            //根据手机宽高设置 viewpager 的大小
            ViewGroup.LayoutParams  params = vp_product.getLayoutParams();
            params.width = needWidth;
            params.height = needWidth;
            vp_product.setLayoutParams(params);
        }
//        Display display = getWindowManager().getDefaultDisplay();
//        Point point = new Point();
//        display.getSize(point);
//        int needWidth =0;
//        int width = point.x;
//        int height = point.y;
//        if(width <height){
//            needWidth = (int) (width*0.6);
//        }else {
//            needWidth = (int) (height*0.6);
//        }


    }

    @Override
    public void onClick(View v) {
        int buyNum;     //购买数量
        switch (v.getId()){
            case R.id.bt_reduce:
                buyNum = Integer.parseInt(tv_buyNum.getText().toString());
                buyNum --;
                if( buyNum <= 1){
                    buyNum = 1; //购买数量最小为0
                }
                //TODO textview不能直接设置int类型数据，会被当做资源id
                tv_buyNum.setText(String.valueOf(buyNum));
                //变量值更新为最新的购买数量
                buyNum = Integer.parseInt(tv_buyNum.getText().toString());
                LogUtil.d(tag,"buyNum_reduce"+buyNum);
                break;
            case R.id.bt_add:
                buyNum = Integer.parseInt(tv_buyNum.getText().toString());
                buyNum ++;
                tv_buyNum.setText(String.valueOf(buyNum));
                //变量值更新为最新的购买数量
                buyNum = Integer.parseInt(tv_buyNum.getText().toString());
                LogUtil.d(tag,"buyNum_add:"+buyNum);
                break;
            case R.id.tv_shopping_car:
                //加入购物车
                //用户选择的当前商品的购买数量
                buyNum = Integer.parseInt(tv_buyNum.getText().toString());
                LogUtil.d(tag,"buyNum_shopping_cart:"+buyNum);
                //保存数据
                saveProductData(buyNum);
                Utils.getInstance().tips(ProductActivity.this,"加入成功！");
                break;
            case R.id.ll_go_to_shopping_car:
                //跳转到购物车界面
                Utils.getInstance().startActivity(ProductActivity.this,ShoppingCartActivity.class);
                break;
        }
    }
    //添加商品图片
    public void initPicture(ProductMsg productMsg){
        ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
        //长宽继承父布局的值
        layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewPager.LayoutParams.MATCH_PARENT;
        ImageView proImageView = new ImageView(this);
        proImageView.setImageResource(ResourceUtils.getMipmapId(context,productMsg.getExtension()));
        proImageView.setLayoutParams(layoutParams);
        //将图片放进viewpager
        views.add(proImageView);
        for (int i: images) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(i);
            imageView.setLayoutParams(layoutParams);
            //将图片放进viewpager
            views.add(imageView);
        }
    }
    //保存数据
    public void saveProductData(int buyNum){
        //创建数据库和表
        Connector.getDatabase();
        LogUtil.d(tag,"saveProductData productMsg :"+productMsg.toString());
        //保存购买商品(正常情况下应该保存在服务器)
        ShoppingProduct product = new ShoppingProduct();
        product.setProductID(productMsg.getProductID());
        product.setProductName(productMsg.getProductName());
        product.setPrice(productMsg.getPrice());
        product.setProductDescription(productMsg.getProductDescription());
        product.setExtension(productMsg.getExtension());
        product.setInventory(productMsg.getInventory());
        //保存商品前，查询数据库是否有与当前 相同的商品，有，则直接数量相加
        List<ShoppingProduct> savedShoppingProducts = DataSupport.select
                ("id","productID","productName","productDescription","type","price","inventory","buyNum","extension")
                .where("productID=?",productMsg.getProductID()+"").find(ShoppingProduct.class);
        //总的购买数量
        LogUtil.d(tag,"saveProductData buyNum :"+buyNum);
        int curBuyProductNum = buyNum;
//        LogUtil.d(tag,"saveProductData size: "+savedShoppingProducts.size());
        if(savedShoppingProducts.size()!= 0){
            //加上商品在数据库已保存的购买数量
            for (ShoppingProduct product1 :savedShoppingProducts) {
//                LogUtil.d(tag,"saveProductData get data :"+ product1.toString());
                curBuyProductNum += product1.getBuyNum();
            }
            product.setBuyNum(curBuyProductNum);
            ContentValues values = new ContentValues();
            values.put("buyNum",curBuyProductNum);
            DataSupport.update(ShoppingProduct.class,values,savedShoppingProducts.get(0).getId());
            LogUtil.d(tag,"saveProductData update buyNum :"+curBuyProductNum);
        }else {
            //数据库没有当前商品，直接保存购买数量及相关信息
            product.setBuyNum(buyNum);
            product.save();
            LogUtil.d(tag,"saveProductData save buyNum");
        }
        LogUtil.d(tag,"saveProductData product:"+product.toString());
    }
}
