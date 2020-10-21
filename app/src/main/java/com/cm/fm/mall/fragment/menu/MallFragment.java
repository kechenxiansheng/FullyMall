package com.cm.fm.mall.fragment.menu;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.cm.fm.mall.R;
import com.cm.fm.mall.activity.ClassifyButtonActivity;
import com.cm.fm.mall.activity.SearchActivity;
import com.cm.fm.mall.activity.ShoppingCartActivity;
import com.cm.fm.mall.adapter.RecycleViewMallAdapter;
import com.cm.fm.mall.adapter.RecycleViewMallGuideAdapter;
import com.cm.fm.mall.bean.ProductMsg;
import com.cm.fm.mall.fragment.BaseFragment;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 商城页面
 */
public class MallFragment extends BaseFragment implements View.OnClickListener {
    public static MallFragment instance;
    RecyclerView rv_products,rv_guide_bar;
    FloatingActionButton bt_shopping_cart;
    LinearLayout ll_mall_search;
    RelativeLayout rl_today_honey;
    ViewFlipper vf_honey_talk;
    ImageView iv_classify_bt,iv_close_honey;
    public RecycleViewMallAdapter adapter;
    public RecycleViewMallGuideAdapter guideAdapter;
    public List<ProductMsg> productMsgs = new ArrayList<>();
    public List<String> titles = new ArrayList<>();
    public List<String> talks = new ArrayList<>();
    public static final int MAIN_ACTIVITY_ID = 1;
    private final int PERMISSION_REQUEST_CODE = 100;
    private final String tag = "TAG_MallFragment";
    private Activity context;

    public static MallFragment getInstance(){
        if(instance == null){
            instance = new MallFragment();
        }
        return instance;
    }

    @Override
    public int getResource() {
        LogUtil.d(tag,"getResource");
        return R.layout.fragment_mall;
    }

    @Override
    public void init(View view) {
        LogUtil.d(tag,"init");
        context = getActivity();
        //初始化视图
        iv_close_honey = view.findViewById(R.id.iv_close_honey);
        iv_classify_bt = view.findViewById(R.id.iv_classify_bt);
        ll_mall_search = view.findViewById(R.id.ll_mall_search);
        rl_today_honey = view.findViewById(R.id.rl_today_honey);
        rv_guide_bar = view.findViewById(R.id.rv_guide_bar);
        rv_products = view.findViewById(R.id.rv_products);
        vf_honey_talk = view.findViewById(R.id.vf_honey_talk);
        bt_shopping_cart = view.findViewById(R.id.bt_shopping_cart);
        //引导栏点击事件监听
        ll_mall_search.setOnClickListener(this);
        //购物车点击监听
        bt_shopping_cart.setOnClickListener(this);
        iv_classify_bt.setOnClickListener(this);
        iv_close_honey.setOnClickListener(this);
        //初始化导航栏标题和数据
        initTitles();
        initAllData();
        /** 导航栏 */
        guideAdapter = new RecycleViewMallGuideAdapter(titles, context, new RecycleViewMallGuideAdapter.GuideItemListener() {
            @Override
            public void onClick(int position) {
                //将当前的item id传回去，以便于item背景色切换
                guideAdapter.setSelectItem(position);
                //根据点击的item展示对应的商品
                showData(position);
            }
        });
        LinearLayoutManager titleManager = new LinearLayoutManager(context);
        titleManager.setOrientation(LinearLayoutManager.HORIZONTAL);     //水平布局
        rv_guide_bar.setLayoutManager(titleManager);
        rv_guide_bar.setAdapter(guideAdapter);

        /** 今日蜜语 */
        for(int i = 0; i < talks.size();i++){
            View cview = LayoutInflater.from(context).inflate(R.layout.layout_honey_talk_item, null);
            TextView tv_honey_talk_msg = cview.findViewById(R.id.tv_honey_talk_msg);
            tv_honey_talk_msg.setText(talks.get(i));
            vf_honey_talk.addView(cview);
        }
        vf_honey_talk.startFlipping();
        vf_honey_talk.setInAnimation(context,R.anim.anim_today_honey_talk_in);
        vf_honey_talk.setOutAnimation(context,R.anim.anim_today_honey_talk_out);
        vf_honey_talk.setAutoStart(true);       //自动加载下一个view
        vf_honey_talk.setFlipInterval(6500);    //轮播时间，毫秒

        /** 数据 */
        adapter = new RecycleViewMallAdapter(productMsgs,context);
        //RecycleView 网格布局
        GridLayoutManager dataManager = new GridLayoutManager(context,3);  //显示3列
        rv_products.setLayoutManager(dataManager);
        rv_products.setAdapter(adapter);
    }

    @Override
    public void loadingData() {

    }

    @Override
    public void dataDestroy() {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 101:
                if(resultCode == Activity.RESULT_OK){
                    String msg = data.getStringExtra("search_msg");
                    //搜索商品
                    search(msg);
                }
                break;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_mall_search:
                //跳转至搜索页面
                Intent intent = new Intent(context,SearchActivity.class);
                intent.putExtra("activityId",PERMISSION_REQUEST_CODE);
                startActivityForResult(intent,101);
                break;
            case R.id.bt_shopping_cart:
                //点击了购物车
                Utils.getInstance().startActivity(context,ShoppingCartActivity.class);
                break;
            case R.id.ll_menu_all_product:
                //点击首页，展示所有商品
                //这一步重新加载全部商品
                initAllData();
                adapter.notifyDataSetChanged();
                break;
            case R.id.iv_classify_bt:
                //点击顶部菜单栏的 分类图标
                Utils.getInstance().startActivity(context,ClassifyButtonActivity.class);
                break;
            case R.id.iv_close_honey:
                //点击今日蜜语的关闭
                rl_today_honey.setVisibility(View.GONE);
                break;
        }
    }

    public void showData(int position){
        String title = titles.get(position);
        LogUtil.d(tag,"showData cur_title : " + title);
        switch (position){
            case 0:
                //首页
                initAllData();
                adapter.notifyDataSetChanged();
                break;
            case 1:
                //推荐
                initProduct1();
                adapter.notifyDataSetChanged();
                break;
            case 2:
                //手机
                initProduct2();
                adapter.notifyDataSetChanged();
                break;
            default:
                Utils.getInstance().tips(context,"点击了"+ title);
                break;
        }

    }

    public void initAllData(){
        productMsgs.clear();

        productMsgs.add(new ProductMsg(1,"华为nova7 se","华为nova7 se 5G手机 银月星辉 全网通（8G+128G）",
                "电子设备|手机",1299.0,1000,"p1"));
        productMsgs.add(new ProductMsg(2,"荣耀30 Pro","荣耀30 Pro 50倍远摄 麒麟990 5G 4000万超感光摄影 3200W美颜自拍 游戏手机 全网通版8GB+128GB 钛空银",
                "电子设备|手机",3999.0,1000,"p2" ));
        productMsgs.add(new ProductMsg(3,"荣耀30","荣耀30 50倍远摄 麒麟985 5G 4000万超广角AI四摄 3200W美颜自拍 全网通版6GB+128GB 钛空银 全面屏手机",
                "电子设备|手机",2689.0,1000,"p3" ));
        productMsgs.add(new ProductMsg(4,"荣耀30S","荣耀30S 麒麟820 5G芯片 3倍光学变焦 20倍数字变焦 全网通版6GB+128GB 蝶羽白",
                "电子设备|手机",2099.0,1000,"p4" ));
        productMsgs.add(new ProductMsg(5,"荣耀20青春版","荣耀20青春版 AMOLED屏幕指纹 4000mAh大电池 20W快充 4800万 手机 4GB+64GB 冰岛幻境",
                "电子设备|手机",989.0,1000,"p5" ));
        productMsgs.add(new ProductMsg(6,"华为nova7 se","华为nova7 se 5G手机 银月星辉 全网通（8G+128G）",
                "电子设备|手机",1299.0,1000,"p6"));
        productMsgs.add(new ProductMsg(7,"荣耀30 Pro","荣耀30 Pro 50倍远摄 麒麟990 5G 4000万超感光摄影 3200W美颜自拍 游戏手机 全网通版8GB+128GB 钛空银",
                "电子设备|手机",3999.0,1000,"p7" ));
        productMsgs.add(new ProductMsg(8,"荣耀30","荣耀30 50倍远摄 麒麟985 5G 4000万超广角AI四摄 3200W美颜自拍 全网通版6GB+128GB 钛空银 全面屏手机",
                "电子设备|手机",2689.0,1000,"p1" ));
        productMsgs.add(new ProductMsg(9,"荣耀30S","荣耀30S 麒麟820 5G芯片 3倍光学变焦 20倍数字变焦 全网通版6GB+128GB 蝶羽白",
                "电子设备|手机",2099.0,1000,"p2" ));
        productMsgs.add(new ProductMsg(10,"荣耀20青春版","荣耀20青春版 AMOLED屏幕指纹 4000mAh大电池 20W快充 4800万 手机 4GB+64GB 冰岛幻境",
                "电子设备|手机",989.0,1000,"p1" ));
        productMsgs.add(new ProductMsg(11,"华为nova7 se","华为nova7 se 5G手机 银月星辉 全网通（8G+128G）",
                "电子设备|手机",1299.0,1000,"p5"));
        productMsgs.add(new ProductMsg(12,"荣耀30 Pro","荣耀30 Pro 50倍远摄 麒麟990 5G 4000万超感光摄影 3200W美颜自拍 游戏手机 全网通版8GB+128GB 钛空银",
                "电子设备|手机",3999.0,1000,"p4" ));
        productMsgs.add(new ProductMsg(13,"荣耀30","荣耀30 50倍远摄 麒麟985 5G 4000万超广角AI四摄 3200W美颜自拍 全网通版6GB+128GB 钛空银 全面屏手机",
                "电子设备|手机",2689.0,1000,"p3" ));
        productMsgs.add(new ProductMsg(14,"荣耀30S","荣耀30S 麒麟820 5G芯片 3倍光学变焦 20倍数字变焦 全网通版6GB+128GB 蝶羽白",
                "电子设备|手机",2099.0,1000,"p7" ));
        productMsgs.add(new ProductMsg(15,"荣耀20青春版","荣耀20青春版 AMOLED屏幕指纹 4000mAh大电池 20W快充 4800万 手机 4GB+64GB 冰岛幻境",
                "电子设备|手机",989.0,1000,"p6" ));
        productMsgs.add(new ProductMsg(16,"华为nova7 se","华为nova7 se 5G手机 银月星辉 全网通（8G+128G）",
                "电子设备|手机",1299.0,1000,"p5"));
        productMsgs.add(new ProductMsg(17,"荣耀30 Pro","荣耀30 Pro 50倍远摄 麒麟990 5G 4000万超感光摄影 3200W美颜自拍 游戏手机 全网通版8GB+128GB 钛空银",
                "电子设备|手机",3999.0,1000,"p4" ));
        productMsgs.add(new ProductMsg(18,"荣耀30","荣耀30 50倍远摄 麒麟985 5G 4000万超广角AI四摄 3200W美颜自拍 全网通版6GB+128GB 钛空银 全面屏手机",
                "电子设备|手机",2689.0,1000,"p3" ));
        productMsgs.add(new ProductMsg(19,"荣耀30S","荣耀30S 麒麟820 5G芯片 3倍光学变焦 20倍数字变焦 全网通版6GB+128GB 蝶羽白",
                "电子设备|手机",2099.0,1000,"p2" ));
        productMsgs.add(new ProductMsg(20,"荣耀20青春版","荣耀20青春版 AMOLED屏幕指纹 4000mAh大电池 20W快充 4800万 手机 4GB+64GB 冰岛幻境",
                "电子设备|手机",989.0,1000,"p1" ));

    }
    public void initProduct1(){
        productMsgs.clear();
        productMsgs.add(new ProductMsg(1,"华为nova7 se","华为nova7 se 5G手机 银月星辉 全网通（8G+128G）",
                "电子设备|手机",1299.0,1000,"p1"));
        productMsgs.add(new ProductMsg(2,"荣耀30 Pro","荣耀30 Pro 50倍远摄 麒麟990 5G 4000万超感光摄影 3200W美颜自拍 游戏手机 全网通版8GB+128GB 钛空银",
                "电子设备|手机",3999.0,1000,"p2" ));
        productMsgs.add(new ProductMsg(3,"荣耀30","荣耀30 50倍远摄 麒麟985 5G 4000万超广角AI四摄 3200W美颜自拍 全网通版6GB+128GB 钛空银 全面屏手机",
                "电子设备|手机",2689.0,1000,"p3" ));
        productMsgs.add(new ProductMsg(4,"荣耀30S","荣耀30S 麒麟820 5G芯片 3倍光学变焦 20倍数字变焦 全网通版6GB+128GB 蝶羽白",
                "电子设备|手机",2099.0,1000,"p4" ));
        productMsgs.add(new ProductMsg(5,"荣耀30S","荣耀30S 麒麟820 5G芯片 3倍光学变焦 20倍数字变焦 全网通版6GB+128GB 蝶羽白",
                "电子设备|手机",1099.0,1000,"p5" ));
    }
    public void initProduct2(){
        productMsgs.clear();
        productMsgs.add(new ProductMsg(1,"荣耀20青春版 ","华为nova7 se 5G手机 银月星辉 全网通（8G+128G）",
                "电子设备|手机",1299.0,1000,"p1"));
        productMsgs.add(new ProductMsg(2,"荣耀30 Pro","荣耀30 Pro 50倍远摄 麒麟990 5G 4000万超感光摄影 3200W美颜自拍 游戏手机 全网通版8GB+128GB 钛空银",
                "电子设备|手机",3999.0,1000,"p2" ));
        productMsgs.add(new ProductMsg(3,"荣耀30","荣耀30 50倍远摄 麒麟985 5G 4000万超广角AI四摄 3200W美颜自拍 全网通版6GB+128GB 钛空银 全面屏手机",
                "电子设备|手机",2689.0,1000,"p3" ));
        productMsgs.add(new ProductMsg(4,"荣耀30S","荣耀30S 麒麟820 5G芯片 3倍光学变焦 20倍数字变焦 全网通版6GB+128GB 蝶羽白",
                "电子设备|手机",2099.0,1000,"p4" ));

    }

    public void initTitles(){
        titles.clear();
        titles.add("首页");
        titles.add("推荐");
        titles.add("手机");
        titles.add("服装");
        titles.add("电器");
        titles.add("化妆品");
        titles.add("酒水");
        titles.add("办公用品");

        talks.clear();
        talks.add("今天的不开心就到此为止吧，明天依然光芒万丈！");
        talks.add("没有什么退路，只有咬牙坚持走下去的路。");
        talks.add("今天的你依旧帅气如初！");
        talks.add("谁都会犯错误，所以人们才会在铅笔的另一头装上橡皮。");
        talks.add("愿十年之后的自己会感谢当初努力奋斗的你。");
        talks.add("日子再甜,也没有你甜！");
        talks.add("喜欢阿羡也喜欢李现，但更喜欢你现（出现）！");
        talks.add("知识不能替代友谊，比起失去你，我宁愿做个白痴！");
        talks.add("世界上最温暖的两个字是，从你口中说出的：晚安。");

    }
    public void search(String searchContent){
        //删除之前用户搜索的商品，并重新加载商品
        initAllData();
        LogUtil.d(tag,"searchContent:" + searchContent);
        if(TextUtils.isEmpty(searchContent)){
            return;
        }
        searchContent = searchContent.trim().toLowerCase();
        Iterator<ProductMsg> iterator= productMsgs.iterator();
        while (iterator.hasNext()){
            ProductMsg productMsg =  iterator.next();

            //TODO 适配器只监听原有的数据list，所以此处筛选，商品类型、商品名、商品描述中都不包含用户搜索的内容 的商品进行删除
            if(!productMsg.getType().toLowerCase().trim().contains(searchContent) && !productMsg.getProductName().toLowerCase().trim().contains(searchContent)
                    && !productMsg.getProductDescription().toLowerCase().trim().contains(searchContent)){
                iterator.remove();
            }
        }
        LogUtil.d(tag,"productMsgs size:"+productMsgs.size());
        LogUtil.d(tag,"productMsgs:"+productMsgs);
        if(productMsgs.size()!=0){
            adapter.notifyDataSetChanged();
            LogUtil.d(tag,"notifyDataSetChanged");
        }else {
            //没找到商品时，productMsgs 已经空了，此时重新加载所有商品
            initAllData();
            adapter.notifyDataSetChanged();
            Utils.getInstance().tips(context,"未找到符合的商品！");
        }
    }

}
