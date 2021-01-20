package com.cm.fm.mall.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseMVPActivity;
import com.cm.fm.mall.contract.activity.AddressDetailContract;
import com.cm.fm.mall.model.adapter.RecycleViewTagAdapter;
import com.cm.fm.mall.model.bean.AddressInfo;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.presenter.activity.AddressDetailPresenter;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.ResourceUtils;
import com.cm.fm.mall.common.util.Utils;
import com.cm.fm.mall.view.customview.SlideButton;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ForwardScope;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 地址信息详页
 */
public class AddressDetailActivity extends BaseMVPActivity<AddressDetailPresenter> implements AddressDetailContract.View,View.OnClickListener,SlideButton.SlideButtonOnCheckedListener{
    private Activity context;
    private ImageView iv_address_back,iv_address_book,iv_delete_address;
    private SlideButton sb_default_address;
    private EditText et_consignee,et_consignee_phone,et_consignee_address,et_consignee_street;
    private RelativeLayout rl_position;
    private TextView tv_address_title,tv_save_address;
    private RecyclerView rv_tag_list;
    private AddressInfo addressInfo;
    private RecycleViewTagAdapter tagAdapter;
    private List<String> tagList = new ArrayList<>();       //标签集合
    private Map<String,Boolean> tagMap = new HashMap<>();   //存储标签选中的状态
    private String selected_tag_text = "";                  //当前选择的标签
    private boolean isDefaultAddress = false;               //是否是默认地址

    private String tag = "TAG_AddressDetailAct";
    @Override
    protected int initLayout() {
        context = this;
        return R.layout.activity_address_detail;
    }

    @Override
    protected void activityAnim() {
        super.activityAnim();
        //告知页面，使用动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
    }

    @Override
    protected void initDataFront() {
        super.initDataFront();
        initData();
    }

    @Override
    protected void initView() {
        iv_address_book = findViewById(R.id.iv_address_book);
        iv_address_back = findViewById(R.id.iv_address_back);
        iv_delete_address = findViewById(R.id.iv_delete_address);
        sb_default_address = findViewById(R.id.sb_default_address);
        tv_address_title = findViewById(R.id.tv_address_title);
        et_consignee = findViewById(R.id.et_consignee);
        et_consignee_phone = findViewById(R.id.et_consignee_phone);
        et_consignee_address = findViewById(R.id.et_consignee_address);
        et_consignee_street = findViewById(R.id.et_consignee_street);
        rl_position = findViewById(R.id.rl_position);                       //定位
        tv_save_address = findViewById(R.id.tv_save_address);
        rv_tag_list = findViewById(R.id.rv_tag_list);

        //滑动按钮，使用小圆样式
        sb_default_address.setSmallCircleModel(getResources().getColor(R.color.colorGrey),getResources().getColor(R.color.colorWhite),
                getResources().getColor(R.color.colorLightBlue11),getResources().getColor(R.color.colorAccent2));

        rl_position.setOnClickListener(this);
        iv_address_back.setOnClickListener(this);
        iv_address_book.setOnClickListener(this);
        tv_save_address.setOnClickListener(this);
        iv_delete_address.setOnClickListener(this);
        sb_default_address.setOnCheckedListener(this);

    }

    @Override
    protected void initDataEnd() {
        super.initDataEnd();
        addressInfo = getIntent().getParcelableExtra("addressInfo");
        LogUtil.d(tag, addressInfo==null ? "添加收货人" : "修改信息");
        sb_default_address.setChecked(false);
        if(addressInfo != null){
            tv_address_title.setText(ResourceUtils.getStringId(context,"address_title_update"));
            iv_delete_address.setVisibility(View.VISIBLE);  //显示删除按钮
            et_consignee.setText(addressInfo.getUsername());
            et_consignee_phone.setText(addressInfo.getPhone());
            et_consignee_address.setText(addressInfo.getAddress());
            et_consignee_street.setText(addressInfo.getStreet());
            if(addressInfo.isDefault()){
                sb_default_address.setChecked(true);
                isDefaultAddress = true;
            }
            //如果当前修改的收货人添加了标签，缓存
            String tag = addressInfo.getTag();
            if(tag != null && !tag.isEmpty()){
                selected_tag_text = tag;
                //将此标签的状态改为true
                for (Map.Entry<String,Boolean> entry:tagMap.entrySet()) {
                    if(entry.getKey().equals(selected_tag_text)){
                        tagMap.put(selected_tag_text,true);
                    }
                }
            }
        }
        LogUtil.d(tag,"cur selected_tag_text : "+selected_tag_text);
        LogUtil.d(tag,"cur tagMap : "+tagMap);
        //标签的适配器
        tagAdapter = new RecycleViewTagAdapter(tagList, context);
        tagAdapter.setChoosed_tag_text(selected_tag_text);
        //自定义的item点击监听
        tagAdapter.setListener(new RecycleViewTagAdapter.MyTagClickListener() {
            @Override
            public void selected(int position) {
                LogUtil.d(tag,"selected position: " + position);
                selected_tag_text = tagList.get(position);
                LogUtil.d(tag,"selected selected_tag_text: " + selected_tag_text);
                boolean isChoose = tagMap.get(selected_tag_text);
                int selected_id = position;
                if(isChoose){
                    //如果当前标签已经选中，但是被再次点击，则回传-1，取消选中状态
                    selected_id = -1;
                    isChoose = false;
                    selected_tag_text = "";
                }else {
                    isChoose = true;
                }
                //适配问题：已有标签被选中时，选择了其他标签，又点击回之前的标签无效（map中的状态没有重置，所以需要点两次）的问题
                for (Map.Entry<String,Boolean> entry:tagMap.entrySet()) {
                    if(entry.getKey().equals(tagList.get(position))){
                        tagMap.put(tagList.get(position),isChoose);
                    }else {
                        tagMap.put(entry.getKey(),false);
                    }
                }
                LogUtil.d(tag,"selected_tag_text2 : "+ selected_tag_text);
                //设置新选中的标签,并刷新适配器
                tagAdapter.setChoose_tag(selected_id);
                tagAdapter.notifyDataSetChanged();
            }
        });
        LogUtil.d(tag,"selected_tag_text : "+ selected_tag_text);
        //设置 LayoutManager
        GridLayoutManager manager = new GridLayoutManager(context,4);   //显示4列
        rv_tag_list.setLayoutManager(manager);
        rv_tag_list.setAdapter(tagAdapter);
    }

    @Override
    protected AddressDetailPresenter createPresenter() {
        return new AddressDetailPresenter();
    }

    @Override
    public void OnSaveResult(int code, String msg) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case MallConstant.ADDRESS_DETAIL_ACTIVITY_CONTACT_CODE:
                if(data != null){
                    List<String> contactInfo = mPresenter.getContactM(data);
                    //显示联系人姓名、电话
                    et_consignee.setText(contactInfo.get(0));
                    et_consignee_phone.setText(contactInfo.get(1));
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_address_back:
                setResult(RESULT_OK);
                context.finish();
                break;
            case R.id.iv_address_book:
                //点击通讯录
                checkPermission();
                break;
            case R.id.iv_delete_address:
                //点击删除按钮（删除收货人，并关闭当前页面）
                DataSupport.delete(AddressInfo.class,addressInfo.getId());
                setResult(RESULT_OK);
                context.finish();
                break;
            case R.id.tv_save_address:
                //点击保存
                String name =  et_consignee.getText().toString();
                String phone =  et_consignee_phone.getText().toString();
                String address =  et_consignee_address.getText().toString();
                String street =  et_consignee_street.getText().toString();
                if(!Utils.getInstance().checkParameter(name,phone,address,street)){
                    Utils.getInstance().tips(context,"收货人信息不完整，请认真填写！");
                    return;
                }
                LogUtil.d(tag,"tagMap"+tagMap);
                //保存信息 以及 修改默认地址
                mPresenter.saveOrUpdateAddressInfoM(addressInfo,name,phone,address,street,isDefaultAddress,selected_tag_text);
                //保存后，直接关闭页面
                setResult(RESULT_OK);
                context.finish();
                break;
            case R.id.rl_position:
                //点击定位
                Utils.getInstance().tips(context,"此功能未开放");
//                Utils.getInstance().startActivity(context,LocationActivity.class);
                break;

        }
    }
    private void initData(){
        tagList.add("家");
        tagList.add("父母");
        tagList.add("亲人");
        tagList.add("公司");
        tagList.add("学校");
        tagList.add("朋友");
        tagList.add("恋人");
        tagList.add("闺蜜");
        tagList.add("死党");
        tagList.add("爱人");
        //默认状态都是未选中
        for (String s:tagList) {
            tagMap.put(s,false);
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
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_OK);
    }

    //默认地址，滑动按钮滑动、点击监听
    @Override
    public void onCheckedChangeListener(boolean isChecked) {
        isDefaultAddress = isChecked;
        LogUtil.d(tag,"isDefaultAddress : " + isDefaultAddress);
    }

    private void checkPermission(){
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_CONTACTS)
                .setDialogTintColor(Color.parseColor("#58a9d7"),Color.parseColor("#58a9d7"))
                /** 如果不添加 取消 按钮的文本，则默认权限为强制需要的权限，会一直弹出跳转设置的提示框，并无法关闭*/
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList,"此功能需要打开通讯录，是否去手动开启通讯录权限？","确定","取消");

                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        //allGranted 是否全部已授权    grantedList 已授权的权限集合        deniedList 已拒绝的权限集合
                        if(allGranted){
                            //打开通讯录
                            mPresenter.openContactM(AddressDetailActivity.this,MallConstant.ADDRESS_DETAIL_ACTIVITY_CONTACT_CODE);
                        }
                    }
                });
    }



}
