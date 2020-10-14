package com.cm.fm.mall.fragment.product;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.activity.ShoppingCartActivity;
import com.cm.fm.mall.activity.UserSelfActivity;
import com.cm.fm.mall.adapter.ViewPagerAdapter;
import com.cm.fm.mall.bean.AddressInfo;
import com.cm.fm.mall.bean.ProductMsg;
import com.cm.fm.mall.bean.ShoppingProduct;
import com.cm.fm.mall.dialog.AddressPageDialog;
import com.cm.fm.mall.fragment.BaseFragment;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.ResourceUtils;
import com.cm.fm.mall.util.Utils;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品信息页
 * 周期 onCreate - onCreateView - onActivityCreated - onStart - onResume
 */
public class ProductInfoFragment extends BaseFragment implements View.OnClickListener {
    Activity context;
    ViewPager vp_product;
    ImageView bt_reduce,bt_add,bt_choose_address;
    TextView tv_product_msg,tv_price,tv_inventory,tv_shopping_car,tv_image_tips,tv_buyNum,tv_obtain_address,tv_buy_now,tv_cur_NumOfType;
    LinearLayout ll_go_to_shopping_car;

    AddressInfo choosedInfo = new AddressInfo();        //选择的地址信息
    boolean haveDefaultAddress = false;                 //是否设置了默认地址
    List<View> views = new ArrayList<>();   //存放展示图片的list
    int[] images = {R.mipmap.p5,R.mipmap.p4,R.mipmap.p2,R.mipmap.p7};
    List<ShoppingProduct> shoppingProducts = new ArrayList<>();
    ProductMsg productMsg; //当前展示的商品实体类

    private String tag ="TAG_ProductInfoFragment";
    private int PRODUCT_INFO_FRAGMENT_ACTIVITY_ID = 1000;
    @Override
    public int getResource() {
        return R.layout.fragment_product_info;
    }
    /**
     * onCreate 执行在 onCreateView 之前
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取传过来的具体商品
        LogUtil.d(tag,"getArguments() is null ? " + (getArguments()==null));
        productMsg = getArguments().getParcelable("productMsg");
        LogUtil.d(tag,"productMsg is null ? " + (productMsg==null));
    }

    @Override
    public void init(View view) {
        context = getActivity();

        tv_image_tips = view.findViewById(R.id.tv_image_tips);   //viewpager的当前item提示
        vp_product = view.findViewById(R.id.vp_product);         //展示商品图片的viewpager
        bt_reduce = view.findViewById(R.id.bt_reduce);           //减按钮
        bt_add = view.findViewById(R.id.bt_add);                 //加按钮
        tv_obtain_address = view.findViewById(R.id.tv_obtain_address);  //配送地址
        bt_choose_address = view.findViewById(R.id.bt_choose_address);  //选择地址的按钮
        tv_product_msg = view.findViewById(R.id.tv_product_msg); //商品描述
        tv_price = view.findViewById(R.id.tv_price);             //商品价格
        tv_inventory = view.findViewById(R.id.tv_inventory);     //库存数量
        tv_buyNum = view.findViewById(R.id.tv_buyNum);           //购买数量
        tv_cur_NumOfType = view.findViewById(R.id.tv_cur_NumOfType);   //当前购买的种类数量
        tv_shopping_car = view.findViewById(R.id.tv_shopping_car);   //加入购物车按钮
        tv_buy_now = view.findViewById(R.id.tv_buy_now);         //立即购买按钮
        ll_go_to_shopping_car = view.findViewById(R.id.ll_go_to_shopping_car);   //去购物车界面按钮

        bt_reduce.setOnClickListener(this);
        bt_add.setOnClickListener(this);
        bt_choose_address.setOnClickListener(this);
        tv_shopping_car.setOnClickListener(this);
        ll_go_to_shopping_car.setOnClickListener(this);
        tv_buy_now.setOnClickListener(this);
        //默认空值
        tv_product_msg.setText("");
        tv_price.setText("￥");
        tv_inventory.setText("");
        //获取传过来的具体商品
        if(productMsg != null){
            LogUtil.d(tag,"productMsg " + productMsg.toString());
            tv_product_msg.setText(productMsg.getProductDescription());
            tv_price.setText("￥"+productMsg.getPrice());
            tv_inventory.setText(String.valueOf(productMsg.getInventory()));
            //数据初始化（包括商品和默认地址的显示）
            initData(productMsg);
        }
        //展示加入购物车的商品种类的数量
        showNumOfType();

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1000:
                if(resultCode == Activity.RESULT_OK){
                    showNumOfType();
                }
                break;
        }
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
            case R.id.bt_choose_address:
                //点击选择地址按钮
                AddressPageDialog dialog = new AddressPageDialog(context,R.style.BottomAddressDialogStyle,choosedInfo, new AddressPageDialog.ChooseListener() {
                    @Override
                    public void chooseResult(AddressInfo info) {
                        choosedInfo = info;
                        tv_obtain_address.setText(String.format("%s%s", choosedInfo.getAddress(), choosedInfo.getStreet()));
                    }
                });
                //点击布局外，隐藏dialog
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                break;
            case R.id.tv_shopping_car:
                //加入购物车
                //用户选择的当前商品的购买数量
                buyNum = Integer.parseInt(tv_buyNum.getText().toString());
                LogUtil.d(tag,"buyNum : "+buyNum);
                //保存数据
                saveProductData(buyNum);
                //重新查询加入购物车的商品
                showNumOfType();
                break;
            case R.id.ll_go_to_shopping_car:
                //跳转到购物车界面
                Intent intent = new Intent(getActivity(),ShoppingCartActivity.class);
                startActivityForResult(intent,1000);
                break;
            case R.id.tv_buy_now:
                //立即购买
                Utils.getInstance().tips(context,"点击了立即购买");
                break;
        }
    }
    //添加商品图片，显示默认地址
    public void initData(ProductMsg productMsg){
        LogUtil.d(tag,"initData");
        /** 商品数据初始化 */
        ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
        //长宽继承父布局的值
        layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewPager.LayoutParams.MATCH_PARENT;
        ImageView proImageView = new ImageView(context);
        proImageView.setImageResource(ResourceUtils.getMipmapId(context,productMsg.getExtension()));
        proImageView.setLayoutParams(layoutParams);
        //将图片放进viewpager
        views.add(proImageView);
        for (int i: images) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(i);
            imageView.setLayoutParams(layoutParams);
            //将图片放进viewpager
            views.add(imageView);
        }
        LogUtil.d(tag,"initPicture views size :" + views.size());

        /** 显示默认地址 */
        List<AddressInfo> addressInfos = DataSupport.findAll(AddressInfo.class);
        if(addressInfos.size()!=0){
            for (AddressInfo info:addressInfos) {
                if(info.isDefault()){
                    haveDefaultAddress = true;
                    choosedInfo = info;
                    tv_obtain_address.setText(String.format("%s%s", info.getAddress(), info.getStreet()));
                    break;
                }
            }
            //如果没有设置默认地址，显示第一条地址
            if(!haveDefaultAddress){
                choosedInfo = addressInfos.get(0);
                tv_obtain_address.setText(String.format("%s%s", addressInfos.get(0).getAddress(), addressInfos.get(0).getStreet()));
            }
        }


    }
    //保存数据
    public void saveProductData(int buyNum){
        //创建数据库和表
        Connector.getDatabase();
        LogUtil.d(tag,"saveProductData productMsg :"+productMsg.toString());
        //保存购买的商品(正常情况下应该保存在服务器)
        ShoppingProduct product = new ShoppingProduct();
        product.setProductID(productMsg.getProductID());
        product.setProductName(productMsg.getProductName());
        product.setPrice(productMsg.getPrice());
        product.setProductDescription(productMsg.getProductDescription());
        product.setExtension(productMsg.getExtension());
        product.setInventory(productMsg.getInventory());
        //保存商品前，查询数据库是否有与当前 相同的商品，有，则直接数量相加
//        DataSupport.find(ShoppingProduct.class,productMsg.getProductID())
        List<ShoppingProduct> savedShoppingProducts = DataSupport.select
                ("id","productID","productName","productDescription","type","price","inventory","buyNum","extension")
                .where("productID=?",productMsg.getProductID()+"").find(ShoppingProduct.class);
        //总的购买数量
        LogUtil.d(tag,"saveProductData buyNum :"+buyNum);
        int curBuyProductNum = buyNum;
        LogUtil.d(tag,"saveProductData size: "+savedShoppingProducts.size());
        if(savedShoppingProducts.size() > 0){
            //加上商品在数据库已保存的购买数量
            for (ShoppingProduct product1 :savedShoppingProducts) {
                LogUtil.d(tag,"saveProductData 当前数据 :"+ product1.toString());
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
            LogUtil.d(tag,"saveProductData save buyNum success");
        }
        LogUtil.d(tag,"saveProductData product info : "+product.toString());
    }

    public void showNumOfType(){
        shoppingProducts = DataSupport.findAll(ShoppingProduct.class);
        //显示购物车中物品种类的数量
        tv_cur_NumOfType.setText(String.valueOf(shoppingProducts.size()));
    }

    public void setDialogAnima(View view){
        /**
         * fromXType
         * fromXValue
         * toXType
         * toXValue
         * fromYType
         * fromYValue
         * toYType
         * toYValue
         */
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        view.startAnimation(animation);
        //动画监听
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void loadingData() {

    }

    @Override
    public void dataDestroy() {

    }
}
