package com.cm.fm.mall.view.fragment.menu;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseMVPFragment;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.Utils;
import com.cm.fm.mall.contract.fragment.MallContract;
import com.cm.fm.mall.model.adapter.RecycleViewMallAdapter;
import com.cm.fm.mall.model.adapter.RecycleViewMallGuideAdapter;
import com.cm.fm.mall.model.bean.ProductMsg;
import com.cm.fm.mall.presenter.fragment.MallPresenter;
import com.cm.fm.mall.view.activity.ClassifyButtonJumpActivity;
import com.cm.fm.mall.view.activity.SearchActivity;
import com.cm.fm.mall.view.activity.ShoppingCartActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * 商城页面
 */
public class MallFragment extends BaseMVPFragment<MallPresenter> implements View.OnClickListener,MallContract.View {
    public static MallFragment instance;
    private RecyclerView rv_products,rv_guide_bar;
    private RefreshLayout refresh_layout;          //下拉刷新开源控件
    private FloatingActionButton bt_shopping_cart;
    private LinearLayout ll_mall_search;
    private RelativeLayout rl_today_honey;
    private ViewFlipper vf_honey_talk;
    private ImageView iv_classify_bt,iv_close_honey;
    private RecycleViewMallAdapter adapter;
    private RecycleViewMallGuideAdapter guideAdapter;
    private List<ProductMsg> productMsgs = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<String> talks = new ArrayList<>();

    private final String tag = "TAG_MallFragment";
    private Activity context;
    private int times = 0;  //加载的次数
    private String[] nameArray = {"华为nova7 se","荣耀30 Pro","荣耀30","荣耀30S","荣耀20青春版"};
    private String[] desArray = {"5G手机 银月星辉 全网通（8G+128G）","50倍远摄 麒麟990 5G 4000万超感光摄影 3200W美颜自拍 游戏手机 全网通版8GB+128GB 钛空银",
            "50倍远摄 麒麟985 5G 4000万超广角AI四摄 3200W美颜自拍 全网通版6GB+128GB 钛空银 全面屏手机","麒麟820 5G芯片 3倍光学变焦 20倍数字变焦 全网通版6GB+128GB 蝶羽白",
            "AMOLED屏幕指纹 4000mAh大电池 20W快充 4800万 手机 4GB+64GB 冰岛幻境"};
    private Double[] priceAray = {1299.0,3999.0,2689.0,2099.0,989.0};
    private String[] imageArray = {"p1","p2","p3","p4","p5","p6","p7"};
    public static MallFragment getInstance(){
        if(instance == null){
            instance = new MallFragment();
        }
        return instance;
    }
    @Override
    protected MallPresenter createPresenter() {
        return new MallPresenter();
    }
    @Override
    public int initLayout() {
        LogUtil.d(tag,"getResource");
        return R.layout.fragment_mall2;
    }

    @Override
    public void initView(View view) {
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
        refresh_layout = view.findViewById(R.id.refresh_layout);
        //引导栏点击事件监听
        ll_mall_search.setOnClickListener(this);
        //购物车点击监听
        bt_shopping_cart.setOnClickListener(this);
        iv_classify_bt.setOnClickListener(this);
        iv_close_honey.setOnClickListener(this);
    }

    @Override
    protected void initDataEnd() {
        super.initDataEnd();
        //初始化导航栏标题和数据
        initTitles();
        initProducts("");
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

        /** 刷新控件设置刷新头部 和 加载尾部 */
        refresh_layout.setRefreshHeader(new ClassicsHeader(context));
        refresh_layout.setRefreshFooter(new ClassicsFooter(context));


        /** 数据 */
        initRefreshLayout();
        adapter = new RecycleViewMallAdapter(productMsgs,context);
        //RecycleView 网格布局
        GridLayoutManager dataManager = new GridLayoutManager(context,3);  //显示3列
        rv_products.setLayoutManager(dataManager);
        rv_products.setAdapter(adapter);
    }

    @Override
    public void dataDestroy() {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case MallConstant.MALL_FRAGMENT_SEARCH_REQUEST_CODE:
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
                intent.putExtra("activityId",MallConstant.MALL_FRAGMENT_PERMISSION_REQUEST_CODE);
                startActivityForResult(intent,MallConstant.MALL_FRAGMENT_SEARCH_REQUEST_CODE);
                break;
            case R.id.bt_shopping_cart:
                //点击了购物车
                Utils.getInstance().startActivity(context,ShoppingCartActivity.class);
                break;
            case R.id.ll_menu_all_product:
                //点击首页，展示所有商品
                //这一步重新加载全部商品
                initProducts("clear");
//                //允许刷新框架加载更多
//                refresh_layout.setEnableLoadMore(true);
                adapter.notifyDataSetChanged();
                break;
            case R.id.iv_classify_bt:
                //点击顶部菜单栏的 分类图标
                Utils.getInstance().startActivity(context,ClassifyButtonJumpActivity.class);
                break;
            case R.id.iv_close_honey:
                //点击今日蜜语的关闭
                rl_today_honey.setVisibility(View.GONE);
                break;
        }
    }

    public void showData(int position){
        //允许框架刷新加载更多
        refresh_layout.setEnableLoadMore(true);
        String title = titles.get(position);
        LogUtil.d(tag,"showData cur_title : " + title);
        switch (position){
            case 0:
                //首页
                initProducts("clear");
                adapter.notifyDataSetChanged();
                break;
            case 1:
                //推荐
                initProducts("clear");
                adapter.notifyDataSetChanged();
                break;
            case 2:
                //手机
                initProducts("clear");
                adapter.notifyDataSetChanged();
                break;
            default:
                Utils.getInstance().tips(context,"点击了"+ title);
                break;
        }

    }

    //刷新控件初始化数据
    public void initRefreshLayout(){
        //刷新
        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //刷新的逻辑//
                initProducts("");
                adapter.notifyDataSetChanged();
                //操作完成通知 RefreshLayout,数据获取完成
                refreshLayout.finishRefresh(1200);    //刷新的延时时间
//                refreshLayout.finishRefresh(1200,true,true);    //延时时间，数据是否获取成功，是否还有更多数据
            }
        });
        //加载更多
        refresh_layout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //加载的逻辑//
                times ++ ;
                initProducts("");
                adapter.notifyDataSetChanged();
                //设置手动加载3次后，提示没有更多了
//                if(times > 3){
//                    refreshLayout.finishLoadMore(1000,true,false);
//                }else {
//                    //操作完成通知 RefreshLayout,数据加载完成
//                    refreshLayout.finishLoadMore(1000,true,true);    //加载的延时时间，数据是否获取成功，是否还有更多数据
//                }
                //操作完成通知 RefreshLayout,数据加载完成
                refreshLayout.finishLoadMore(1000,true,true);    //加载的延时时间，数据是否获取成功，是否还有更多数据

            }
        });
        //添加头部（可以添加多个）
//        View headerView = LayoutInflater.from(context).inflate(R.layout.layout_xrv_header,(ViewGroup)context.findViewById(android.R.id.content),false);
//        headerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.getInstance().tips(context,"点击了头部");
//            }
//        });

        //设置打开时自动刷新
//        refresh_layout.autoRefresh();
    }

    public void initProducts(String type){
        if("clear".equals(type) ){
            productMsgs.clear();
        }
        //每次只加载 15 条数据
        for(int i = 1; i <= 15 ;i++){
            int nameIndex = (int)Math.round(Math.random()* (nameArray.length-1));
            int desIndex = (int)Math.round(Math.random()* (desArray.length-1));
            int priceIndex = (int)Math.round(Math.random()* (priceAray.length-1));
            int imageIndex = (int)Math.round(Math.random()* (imageArray.length-1));
            productMsgs.add(new ProductMsg(i,nameArray[nameIndex],desArray[desIndex],"电子设备|手机",
                    priceAray[priceIndex],1000,imageArray[imageIndex]));
        }
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
        talks.add("今天的你依旧帅气/美丽如初！");
        talks.add("谁都会犯错误，所以人们才会在铅笔的另一头装上橡皮。");
        talks.add("愿十年之后的自己会感谢当初努力奋斗的你。");
        talks.add("日子再甜,也没有你甜！");
        talks.add("喜欢阿羡也喜欢李现，但更喜欢你现（出现）！");
        talks.add("知识不能替代友谊，比起失去你，我宁愿做个白痴！");
        talks.add("世界上最温暖的两个字是，从你口中说出的：晚安。");
    }

    //注意：此处返回对象 products 就是  productMsgs 对象，内部用的迭代器删除数据，用 == 号，equals 判断两个对象，都是true
    @Override
    public void OnSearchResult(int code, List<ProductMsg> products) {
        LogUtil.d(tag,"code: "+code +",products: " + products );
        switch (code){
            case MallConstant.SUCCESS:
                LogUtil.d(tag,"搜索成功");
                if(products.size()!=0){
                    //这里只显示搜索到的商品，不允许刷新框架继续刷新
                    refresh_layout.setEnableLoadMore(false);
                    LogUtil.d(tag,"notifyDataSetChanged");
                }else {
                    //没找到商品时，进行提示
                    products.clear();
                    Utils.getInstance().tips(context,"未找到符合的商品！");
                }
                break;
            case MallConstant.FAIL:
                LogUtil.e(tag,"搜索失败，view未绑定");
                break;
        }
        adapter.notifyDataSetChanged();
    }
    //搜索
    public void search(String searchContent){
        //删除之前用户搜索的商品，并重新加载商品
        LogUtil.d(tag,"searchContent:" + searchContent);
        if(TextUtils.isEmpty(searchContent)){
            return;
        }
        mPresenter.search(productMsgs, searchContent);
    }

}
