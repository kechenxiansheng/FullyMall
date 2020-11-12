package com.cm.fm.mall.presenter.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.contract.activity.AddressDetailContract;
import com.cm.fm.mall.model.bean.AddressInfo;
import com.cm.fm.mall.model.model.activity.AddressDetailModel;

import java.util.ArrayList;
import java.util.List;

public class AddressDetailPresenter extends BasePresenter<AddressDetailContract.Model,AddressDetailContract.View> implements AddressDetailContract.Presenter {

    private String tag = "TAG_AddressDetailPresenter";

    @Override
    protected AddressDetailContract.Model createModule() {
        return new AddressDetailModel();
    }

    @Override
    public void init() {

    }

    @Override
    public void openContactM(Activity activity, int requestCode) {
        if(isViewBind()){
            //打开联系人界面
            Intent intent = new Intent();
            intent.setAction("android.intent.action.PICK");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setType("vnd.android.cursor.dir/phone_v2");
            activity.startActivityForResult(intent, requestCode);
        }

    }

    @Override
    public List<String> getContactM(Intent data) {
        //获取当前选择的联系人信息
        Uri uri = data.getData();
        ContentResolver contentResolver = getContext().getContentResolver();
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
        //联系人姓名、电话
        List<String> contactInfo = new ArrayList<>();
        contactInfo.add(name.replace(" ",""));
        contactInfo.add(phoneNum.replace(" ",""));
        return contactInfo;
    }


    @Override
    public void saveOrUpdateAddressInfoM(AddressInfo curAddressInfo,String name,String phone,String address,
                String street,boolean isDefaultAddress,String selectedTagText) {
            if(isViewBind()){
                getModel().saveOrUpdateAddressInfoM(curAddressInfo,name,phone,address,street,isDefaultAddress,selectedTagText);
            }

    }


}
