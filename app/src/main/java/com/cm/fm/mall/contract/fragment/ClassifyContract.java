package com.cm.fm.mall.contract.fragment;

import android.app.Activity;
import android.content.Context;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.base.ResponseCallback;

public interface ClassifyContract {
    interface Model extends IBaseModel {
        String getJsonData(Context context, String fileName);
    }

    interface View extends IBaseView {

    }

    interface Presenter{
        String getJsonData(String fileName);
    }
}
