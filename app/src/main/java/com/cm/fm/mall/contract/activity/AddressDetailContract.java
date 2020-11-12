package com.cm.fm.mall.contract.activity;

import android.app.Activity;
import android.content.Intent;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.model.bean.AddressInfo;

import java.util.List;

/**
 * 暂时没有可执行的方法，mvp 扩展备用
 */
public interface AddressDetailContract {

    interface Model extends IBaseModel {
        void saveOrUpdateAddressInfoM(AddressInfo curAddressInfo,String name,String phone,String address,String street,boolean isDefaultAddress,String selectedTagText);
    }

    interface View extends IBaseView {
        //回调暂时未用到
        void OnSaveResult(int code,String msg);
    }

    interface Presenter{
        void saveOrUpdateAddressInfoM(AddressInfo curAddressInfo,String name,String phone,String address,String street,boolean isDefaultAddress,String selectedTagText);
        void openContactM(Activity activity,int requestCode);
        List<String> getContactM(Intent data);
    }
}
