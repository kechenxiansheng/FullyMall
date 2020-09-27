package com.cm.fm.mall.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.bean.UserInfo;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.Utils;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ForwardScope;

import org.litepal.crud.DataSupport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.provider.MediaStore.EXTRA_OUTPUT;

/**
 * 头像展示页面
 * 1、activityId : 6
 * 2、本页所有请求码以 600 开始
 */
public class HeadPortraitActivity extends BaseActivity implements View.OnClickListener {

    private Activity context;

    private LinearLayout ll_change_method;
    private ImageView iv_show_head;
    private TextView tv_take_photo,tv_photo_album,tv_head_back,tv_change_photo;
    private Uri imageUri;
    private Bitmap bitmap;      //新头像位图

    public static final int HEAD_PORTRAIT_ACTIVITY_ID = 6;
    private static final int TAKE_PHOTO = 602;
    private static final int ALBUM_CODE = 601;
    private static final int PERMISSION_CODE = 600;
    private final String tag = "TAG_HeadPortraitActivity";

    private boolean SURE_CHANGE = false;    //false 此时其他按钮是隐藏状态  true 其他按钮是显示状态

    List<UserInfo> userInfos;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //使用动画
        Utils.getInstance().actUseAnim(context,R.transition.fade);
        setContentView(R.layout.activity_head_portrait);

        ll_change_method = findViewById(R.id.ll_change_method);
        iv_show_head = findViewById(R.id.iv_show_head);
        tv_head_back = findViewById(R.id.tv_head_back);
        tv_take_photo = findViewById(R.id.tv_take_photo);
        tv_photo_album = findViewById(R.id.tv_photo_album);
        tv_change_photo = findViewById(R.id.tv_change_photo);

        userInfos = DataSupport.findAll(UserInfo.class);
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

        tv_take_photo.setOnClickListener(this);
        tv_photo_album.setOnClickListener(this);
        tv_head_back.setOnClickListener(this);
        tv_change_photo.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(tag,"requestCode:"+requestCode+"  resultCode:"+resultCode+"  data:"+(data==null));
        switch (requestCode ){
            case TAKE_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    try {
                        //将图片显示出来
                        if(data != null){
                            Uri image_uri = data.getParcelableExtra(EXTRA_OUTPUT);
                            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(image_uri));
                            //设置固定大小
                            bitmap = ThumbnailUtils.extractThumbnail(bitmap,300,300);
                            bitmap = Utils.getInstance().createCircleBitmap(bitmap);
                            iv_show_head.setImageBitmap(bitmap);

                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ALBUM_CODE:
                //处理相册的选择结果
                if(resultCode == RESULT_OK){
                    //如果api大于19
                    if(data != null){
                        handleImageOnKitKat(data);
                    }
                }
                break;
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        LogUtil.d(tag,"permission,requestCode:"+requestCode+",permissions:"+Arrays.toString(permissions)+",grantResults:"+Arrays.toString(grantResults));
//        switch (requestCode){
//            case PERMISSION_CODE:
//                //处理申请权限后的结果
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    LogUtil.d(tag,"agreed permission");
//                    //同意了权限,打开相册
//                    openAlbum();
//                }else {
//                    Utils.getInstance().tips(context,"您拒绝了相册权限");
//                    LogUtil.d(tag,"用户未同意权限");
//                }
//                break;
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_take_photo:
                //TODO 拍照
                takephoto();
                break;
            case R.id.tv_photo_album:
                //TODO 相册
                //没给存储权限先申请
                LogUtil.d(tag,"start checkSelfPermission");
//                if((ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE))
//                        != PackageManager.PERMISSION_GRANTED){
//                    LogUtil.d(tag,"start requestPermissions");
//                    ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_CODE);
//                }else {
//                    LogUtil.d(tag,"start open album");
//                    //有权限直接打开相册
//                    openAlbum();
//                }
                checkPermission();
                break;
            case R.id.tv_change_photo:
                if(!SURE_CHANGE){
                    ll_change_method.setVisibility(View.VISIBLE);
                    //提示修改为 确认更换
                    tv_change_photo.setText("确定更换");
                    SURE_CHANGE = true;
                }else {
                    //点击了 确认更换，保存头像。提示变回更换头像，并隐藏相机和相册
                    LogUtil.d(tag,"点击了 确认更换,bitmap is null? "+(bitmap==null));
                    if(bitmap!=null){
                        savePhoto(bitmap);
                    }
                    ll_change_method.setVisibility(View.GONE);
                    tv_change_photo.setText("更换头像");
                    SURE_CHANGE = false;
                    setResult(RESULT_OK);
                }

                break;
            case R.id.tv_head_back:
                //TODO 点击返回
                setResult(RESULT_OK);
                context.finish();
                break;
        }
    }
    private void checkPermission(){
        //由于本页是继承的activity，需要强转为 FragmentActivity
        PermissionX.init(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                            //有权限直接打开相册
                            openAlbum();
                        }
                    }
                });
    }
    /**
     * 拍照
     * getExternalCacheDir 获取当前应用存放临时缓存数据的目录
     * MediaStore.EXTRA_OUTPUT  意图名称，值：output。用于指示要使用的内容解析器Uri存储请求的图像或视频。
     */
    public void takephoto(){
        //存储拍好的照片
        LogUtil.d(tag,"takephoto,path:"+Objects.requireNonNull(context.getExternalCacheDir()).getAbsolutePath());
        File outputImage = new File(context.getExternalCacheDir(),"tp_head_image.jpg");
        LogUtil.d(tag,"outputImage:"+outputImage);
        //如果已存在，先删除
        if(outputImage.exists()){
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 24){
            //这里使用了内容提供器，所以需要在manifest注册
            imageUri = FileProvider.getUriForFile(context,"com.cm.fm.mall.provider",outputImage);
        }else {
            imageUri = Uri.fromFile(outputImage);
        }
        LogUtil.d(tag,"takephoto,imageUri:"+imageUri);
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //MediaStore.EXTRA_OUTPUT  意图名称，值：output。表示要使用的内容解析器Uri存储请求的是图像或视频。
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }

    /**
     * 打开相册
     * */
    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");      //过滤所有图片
        startActivityForResult(intent,ALBUM_CODE);
    }

    /**
     *  api>=19,处理相册选择的图片
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data){
        String imagePath = "";
        Uri uri = data.getData();
        LogUtil.d(tag,"相册选择的图片 uri:"+ uri);
        //如果是Document 类型的uri，则用DocumentsContract 处理
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if(uri != null && "com.android.providers.media.documents".equals(uri.getAuthority())){
                //解析出数字格式的id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if(uri != null && "com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri conentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(conentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是普通方式，则用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的uri,则直接获取图片路径
            imagePath = uri.getPath();
        }
        // /storage/emulated/0/DCIM/Camera/IMG_20200712_175717.jpg
        LogUtil.d(tag,"imagePath:"+imagePath);
        //根据路径显示图片
        displayImage(imagePath);
    }

    //api<19，处理图片方式
    private void handleImageOnBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    /** 获取照片路径 */
    private String getImagePath(Uri uri,String selection){
        String path = "";
        //通过内容接收器查询
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        LogUtil.d(tag,"getImagePath,path:"+path);
        return path;
    }

    /** 显示照片 */
    private void displayImage(String imagePath){
        // /storage/emulated/0/DCIM/Camera/IMG_20200712_175717.jpg
        if(!imagePath.isEmpty()){
            bitmap = BitmapFactory.decodeFile(imagePath);
            //设置固定大小
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,300,300);
            bitmap = Utils.getInstance().createCircleBitmap(bitmap);
            iv_show_head.setImageBitmap(bitmap);
        }else {
            LogUtil.d(tag,"imagePath 为空");
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
    /**  保存文件  */
    public File savePhoto(Bitmap bitmap) {

//        String path = Environment.getExternalStorageDirectory().toString()+"/fullyMall/icon_bitmap/head_photo.jpg";
        String path = getExternalFilesDir(DIRECTORY_PICTURES)+"/head_photo.jpg";
        //   savePhoto path:/storage/emulated/0/Android/data/com.cm.fm.mall/files/Pictures/head_photo.jpg
        LogUtil.d(tag,"savePhoto path:"+path);
        File headPhoto = new File(path);
        if(headPhoto.exists()){
            headPhoto.delete();
        }
        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(headPhoto));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream!=null){
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return headPhoto;
    }
}
