package com.cm.fm.mall.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;

import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseMVPActivity;
import com.cm.fm.mall.contract.activity.HeadPortraitContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.presenter.activity.HeadPortraitPresenter;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.Utils;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ForwardScope;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * 头像展示页面
 * 1、activityId : 6
 * 2、本页所有请求码以 600 开始
 */
public class HeadPortraitActivity extends BaseMVPActivity<HeadPortraitPresenter> implements HeadPortraitContract.View,View.OnClickListener {
    private Activity context;

    private LinearLayout ll_change_method;
    private ImageView iv_show_head;
    private TextView tv_take_photo,tv_photo_album,tv_head_back;
    private ToggleButton tb_change_photo;
    private Bitmap bitmap;      //新头像位图

    private final String tag = "TAG_HeadActivity";

    private Uri imageUri;
    List<UserInfo> userInfos;       //用户信息，暂时未使用

    @Override
    protected void activityAnim() {
        super.activityAnim();
        //使用动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
    }

    @Override
    protected int initLayout() {
        context = this;
        return R.layout.activity_head_portrait;
    }

    @Override
    protected void initView() {
        ll_change_method = findViewById(R.id.ll_change_method);
        iv_show_head = findViewById(R.id.iv_show_head);
        tv_head_back = findViewById(R.id.tv_head_back);
        tv_take_photo = findViewById(R.id.tv_take_photo);
        tv_photo_album = findViewById(R.id.tv_photo_album);
        tb_change_photo = findViewById(R.id.tb_change_photo);

        userInfos = DataSupport.findAll(UserInfo.class);

        tv_take_photo.setOnClickListener(this);
        tv_photo_album.setOnClickListener(this);
        tv_head_back.setOnClickListener(this);
        tb_change_photo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    //点击了 确认更换，保存头像。提示变回更换头像，并隐藏相机和相册
                    LogUtil.d(tag,"点击了 确认更换,bitmap is null? "+(bitmap==null));
                    ll_change_method.setVisibility(View.GONE);
                    /** 保存图片 */
                    if(bitmap != null){
                        mPresenter.savePhoto(bitmap);
                    }
                }else {
                    ll_change_method.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void initDataEnd() {
        super.initDataEnd();
        //有头像直接展示
        String path = getExternalFilesDir(DIRECTORY_PICTURES)+"/head_photo.jpg";
        if (new File(path).exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            bitmap = Utils.getInstance().createCircleBitmap(bitmap);
            iv_show_head.setImageBitmap(bitmap);
            iv_show_head.setScaleType(ImageView.ScaleType.CENTER_CROP);//将原图按比例放大至 ImageView 大小
        }else {
            //使用默认图片
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.head_photo);
            bitmap = Utils.getInstance().createCircleBitmap(bitmap);
            iv_show_head.setImageBitmap(bitmap);
        }
    }

    @Override
    protected HeadPortraitPresenter createPresenter() {
        return new HeadPortraitPresenter();
    }

    /**
     * 调用系统相机的bug：
     * 相机有自己的默认存储路径，将会通过data返回，如果自己定义了路径，则data不会有数据，
     * 但有些机型不自定义路径同样data为null，所以最好是直接使用路径获取 bitmap）
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(tag,"requestCode:"+requestCode+"  resultCode:"+resultCode+"  data:"+(data==null));
        switch (requestCode ){
            case MallConstant.HEAD_PORTRAIT_ACTIVITY_TAKE_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    /** 将拍摄的照片显示出来 */
                    if(imageUri != null){
                        bitmap = createCircleBitmap(imageUri);
                    }
                    LogUtil.d(tag,"head image bitmap is null ? " + (bitmap==null));
                    if(bitmap != null){
                        iv_show_head.setImageBitmap(bitmap);
                    }
                }
                break;
            case MallConstant.HEAD_PORTRAIT_ACTIVITY_ALBUM_CODE:
                /** 处理相册选择的照片，并显示出来 */
                if(resultCode == RESULT_OK){
                    if(data != null){
                        mPresenter.handleImage(data);
                    }
                }
                break;
        }
    }
    //修改为圆形图片
    private Bitmap createCircleBitmap(Uri imageUri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //设置固定大小
        bitmap = ThumbnailUtils.extractThumbnail(bitmap,300,300);
        bitmap = Utils.getInstance().createCircleBitmap(bitmap);

        return bitmap;
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
        setResult(RESULT_OK);
        super.onDestroy();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_take_photo:
                //TODO 拍照
                LogUtil.d(tag,"start checkPermission : takePhoto");
                checkCameraPermission();
                break;
            case R.id.tv_photo_album:
                //TODO 相册
                LogUtil.d(tag,"start checkPermission : album");
                checkAlbumPermission();
                break;
            case R.id.tv_head_back:
                //TODO 点击返回
                setResult(RESULT_OK);
                context.finish();
                break;
        }
    }
    //检测相机权限
    private void checkCameraPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            PermissionX.init(HeadPortraitActivity.this)
                    .permissions(Manifest.permission.CAMERA)
                    .setDialogTintColor(Color.parseColor("#58a9d7"),Color.parseColor("#58a9d7"))
                    /** 如果不添加 取消 按钮的文本，则默认权限为强制需要的权限，会一直弹出跳转设置的提示框，并无法关闭*/
                    .onForwardToSettings(new ForwardToSettingsCallback() {
                        @Override
                        public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                            scope.showForwardToSettingsDialog(deniedList,"此功能需要使用相机权限，是否去手动开启权限？","确定","取消");
                        }
                    })
                    .request(new RequestCallback() {
                        @Override
                        public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                            //allGranted 是否全部已授权    grantedList 已授权的权限集合        deniedList 已拒绝的权限集合
                            if(allGranted){
                                //权限通过则打开相机
                                imageUri = mPresenter.takePhoto(context,MallConstant.HEAD_PORTRAIT_ACTIVITY_TAKE_PHOTO);
                            }
                        }
                    });
        }else {
            imageUri = mPresenter.takePhoto(context,MallConstant.HEAD_PORTRAIT_ACTIVITY_TAKE_PHOTO);
        }

    }
    //检测相册权限
    private void checkAlbumPermission(){
        PermissionX.init(HeadPortraitActivity.this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .setDialogTintColor(Color.parseColor("#58a9d7"),Color.parseColor("#58a9d7"))
                /** 如果不添加 取消 按钮的文本，则默认权限为强制需要的权限，会一直弹出跳转设置的提示框，并无法关闭*/
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList,"此功能需要使用存储，是否去手动开启存储权限？","确定","取消");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        //allGranted 是否全部已授权    grantedList 已授权的权限集合        deniedList 已拒绝的权限集合
                        if(allGranted){
                            //权限通过则打开相册
                            mPresenter.openAlbum(context,MallConstant.HEAD_PORTRAIT_ACTIVITY_ALBUM_CODE);
                        }
                    }
                });
    }

    @Override
    public void OnShowImage(Bitmap bitmap) {
        /** 展示相册选择的照片 */
        iv_show_head.setImageBitmap(bitmap);
        //实例化照片的全局变量bitmap，点击更换按钮时需要保存此图片
        this.bitmap = bitmap;
    }


}
