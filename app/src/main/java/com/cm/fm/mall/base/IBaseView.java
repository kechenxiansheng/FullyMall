package com.cm.fm.mall.base;

import android.content.Context;

/**
 * 父view，定义通用的抽象方法
 */
public interface IBaseView {
   /** 显示加载框 */
   void showLoading();
   /** 隐藏加载框 */
   void hideLoading();
   /** 提示 */
   void toast(String msg);
   /** 上下文 */
   Context getContext();
}
