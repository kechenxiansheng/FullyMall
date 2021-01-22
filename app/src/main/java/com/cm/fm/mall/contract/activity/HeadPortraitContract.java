package com.cm.fm.mall.contract.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.common.Callback;

public interface HeadPortraitContract {

    interface Model extends IBaseModel {
        void saveHeadPortrait(String account,Bitmap bitmap,Callback callback);
    }

    interface View extends IBaseView {
        void OnShowImage(Bitmap bitmap);
        void OnSavePhotoResult(int code,String msg);
    }

    interface Presenter{
        //打开相册
        void openAlbum(Activity activity,int code);
        //处理照片
        void handleImage(Intent data);
        //拍照
        Uri takePhoto(Activity activity, int code);
        //保存照片
        void savePhoto(String account,Bitmap bitmap);
    }
}
