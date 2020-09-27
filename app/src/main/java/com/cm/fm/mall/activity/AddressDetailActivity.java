package com.cm.fm.mall.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cm.fm.mall.R;
import com.cm.fm.mall.adapter.RecycleViewTagAdapter;
import com.cm.fm.mall.bean.AddressInfo;
import com.cm.fm.mall.customview.GridViewForScrollView;
import com.cm.fm.mall.layout.SlideButton;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.ResourceUtils;
import com.cm.fm.mall.util.Utils;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallbackWithBeforeParam;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地址详情页面
 * consignee 收货人
 */
public class AddressDetailActivity extends BaseActivity implements View.OnClickListener,SlideButton.SlideButtonOnCheckedListener {
    Activity context;
    ImageView iv_address_back,iv_address_book,iv_delete_address;
    SlideButton sb_default_address;
    EditText et_consignee,et_consignee_phone,et_consignee_address,et_consignee_street;
    RelativeLayout rl_position;
    TextView tv_address_title,tv_save_address;
    RecyclerView rv_tag_list;
    AddressInfo addressInfo;
    RecycleViewTagAdapter tagAdapter;
    List<String> tagList = new ArrayList<>();   //标签集合
    Map<String,Boolean> tagMap = new HashMap<>();   //标签选中的状态
    String selected_tag_text = "";    //当前选择的tag
    boolean isDefaultAddress = false;   //是否是默认地址

    private String tag = "TAG_AddressDetailAct";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //告知页面，使用动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
        setContentView(R.layout.activity_address_detail);
        init();
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
        //设置manager
        GridLayoutManager manager = new GridLayoutManager(context,4);   //显示4列
        rv_tag_list.setLayoutManager(manager);
        rv_tag_list.setAdapter(tagAdapter);

    }
    private void init(){
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
                if(addressInfo!=null){
                    /** 点击编辑按钮进来，需要修改 */
                    AddressInfo data = DataSupport.find(AddressInfo.class,addressInfo.getId());
                    data.setUsername(name);
                    data.setPhone(phone);
                    data.setAddress(address);
                    data.setStreet(street);
                    data.setDefault(isDefaultAddress);
                    data.setTag(selected_tag_text);
                    LogUtil.d(tag,data.toString());
                    updateDefaultAddress(1,data);
                }else {
                    /** 新建地址进来的，直接保存 */
                    AddressInfo info = new AddressInfo();
                    info.setUsername(name);
                    info.setPhone(phone);
                    info.setAddress(address);
                    info.setStreet(street);
                    info.setDefault(isDefaultAddress);
                    info.setTag(selected_tag_text);
                    LogUtil.d(tag,info.toString());
                    updateDefaultAddress(0,info);
                }
                setResult(RESULT_OK);
                //保存后，直接关闭页面
                context.finish();
                break;
            case R.id.rl_position:
                //点击定位
                Utils.getInstance().tips(context,"此功能未开放");
//                Utils.getInstance().startActivity(context,LocationActivity.class);
                break;

        }
    }

    /**
     * 重置所有地址的默认状态为false
     * @param type 1 修改  0 新加
     * @param info
     */
    private void updateDefaultAddress(int type,AddressInfo info){
        //不管是新加还是修改，先直接保存
        info.save();
        //修改信息时没有设置为默认地址，则不重置其他收货人的默认地址记录
        if(type == 1  && !isDefaultAddress){
            return;
        }
        //遍历所有的，将非当前收货人 都设置默认为false
        List<AddressInfo> addressInfos = DataSupport.findAll(AddressInfo.class);
        for (AddressInfo ai:addressInfos) {
            if(info.getId() != ai.getId()){
                ai.setDefault(false);
                ai.save();
            }
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 901:
                if(data != null){
                    //获取选择的当前联系人信息
                    Uri uri = data.getData();
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = null;
                    String name = "";
                    String phoneNum = "";
                    if(uri != null){
                        cursor = contentResolver.query(uri,new String[]{"display_name","data1"},null,null,null);
                    }
                    if(cursor != null){
                        while (cursor.moveToNext()){
                            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                    }
                    //显示联系人姓名、电话
                    et_consignee.setText(name.replace(" ",""));
                    et_consignee_phone.setText(phoneNum.replace(" ",""));
                }
                break;
        }
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
                            openContact();
                        }
                    }
                });
    }
    //打开通讯录
    private void openContact(){
        // 跳转到联系人界面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, 901);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //物理返回键关闭本页，也需要回传数据
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
        }
        return super.onKeyDown(keyCode, event);
    }

    //默认地址，滑动按钮滑动、点击监听
    @Override
    public void onCheckedChangeListener(boolean isChecked) {
        isDefaultAddress = isChecked;
        LogUtil.d(tag,"isDefaultAddress : " + isDefaultAddress);
    }
}
