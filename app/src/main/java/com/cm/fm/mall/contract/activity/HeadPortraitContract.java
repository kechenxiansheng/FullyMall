package com.cm.fm.mall.contract.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;

public interface HeadPortraitContract {

    interface Model extends IBaseModel {

    }

    interface View extends IBaseView {
        void OnShowImage(Bitmap bitmap);
    }

    interface Presenter{
        //打开相册
        void openAlbum(Activity activity,int code);
        //处理照片
        void handleImage(Intent data);
        //拍照
        Uri takePhoto(Activity activity, int code);
        //保存照片
        void savePhoto(Bitmap bitmap);
    }
}
