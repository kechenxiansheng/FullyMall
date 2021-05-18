package com.cm.fm.mall.presenter.activity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.common.util.ImageUtil;
import com.cm.fm.mall.contract.activity.HeadPortraitContract;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.model.model.activity.HeadPortraitModel;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import androidx.core.content.FileProvider;

import static android.os.Environment.DIRECTORY_PICTURES;

public class HeadPortraitPresenter extends BasePresenter<HeadPortraitContract.Model,HeadPortraitContract.View> implements HeadPortraitContract.Presenter {
    private final String TAG = "FM_HeadPresenter";

    @Override
    protected HeadPortraitContract.Model createModule() {
        return new HeadPortraitModel();
    }

    @Override
    public void init() {

    }
    /** 打开相册 */
    @Override
    public void openAlbum(Activity activity, int code) {
        if(isViewBind()){
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");      //过滤所有图片
            activity.startActivityForResult(intent,code);
        }
    }
    /** 处理相册选择的图片 */
    @Override
    public void handleImage(Intent data) {
        if(isViewBind()){
            if(Build.VERSION.SDK_INT < 19){     //提示是因为我的最小为21，不可能小于19
                handleImageOnBefore19(data);
                return;
            }
            /** api>=19 */
            String imagePath = "";
            Uri uri = data.getData();
            LogUtil.d(TAG,"相册选择的图片 uri:"+ uri);
            //如果是Document 类型的uri，则用DocumentsContract 处理
            if(DocumentsContract.isDocumentUri(getContext(),uri)){
                String docId = DocumentsContract.getDocumentId(uri);
                if(uri != null && "com.android.providers.media.documents".equals(uri.getAuthority())){
                    //解析出数字格式的id
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
                }else if(uri != null && "com.android.providers.downloads.documents".equals(uri.getAuthority())){
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                    imagePath = getImagePath(contentUri,null);
                }
            }else if ("content".equalsIgnoreCase(uri.getScheme())){
                //如果是普通方式，则用普通方式处理
                imagePath = getImagePath(uri,null);
            }else if("file".equalsIgnoreCase(uri.getScheme())){
                //如果是file类型的uri,则直接获取图片路径
                imagePath = uri.getPath();
            }
            // /storage/emulated/0/DCIM/Camera/IMG_20200712_175717.jpg
            LogUtil.d(TAG,"imagePath:"+imagePath);
            //根据路径显示图片
            displayImage(imagePath);
        }

    }

    /**
     * 拍照
     * getExternalCacheDir() 获取当前应用存放临时缓存数据的目录（对应：清除缓存　选项）
     * getExternalFilesDir(type)   获取当前应用较长时间保存的数据，参数 type 指定文件类型（对应：清除数据　选项）
     * 注意：两种方式的使用，如果自定义了路径需跟 file_paths.xml 文件的配置一致
     * MediaStore.EXTRA_OUTPUT  意图名称，值：output。用于指示要使用的内容解析器Uri存储请求的图像或视频。
     */
    @Override
    public Uri takePhoto(Activity activity, int code) {
        if(isViewBind()){
            //存储拍好的照片
            if(!Utils.hasCamera(activity)){
                Utils.tips(activity,"当前设备不支持拍照");
                return null;
            }
            Uri imageUri;
            if(Build.VERSION.SDK_INT >= 29){
                /** android 10 以上获取uri */
                String status = Environment.getExternalStorageState();
                // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
                LogUtil.d(TAG,"外部存储 ？" + (status.equals(Environment.MEDIA_MOUNTED)));
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                } else {
                    imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
                }
                LogUtil.d(TAG,"android 10,imageUri : " + imageUri);
                //content://media/external/images/media/368231
            }else {
                LogUtil.d(TAG,"takephoto,path:"+Objects.requireNonNull(activity.getExternalFilesDir(DIRECTORY_PICTURES)).getAbsolutePath());
                //创建目录
                File storageDir = activity.getExternalFilesDir(DIRECTORY_PICTURES);
                if (storageDir != null && !storageDir.exists()) {
                    storageDir.mkdir();
                }
                File imageFile = new File(storageDir,"tp_head_image.jpg");
                LogUtil.d(TAG,"imageFile:"+imageFile);
                //如果头像已存在，先删除
                if(imageFile.exists()){
                    boolean delete = imageFile.delete();
                    LogUtil.d(TAG,"图片删除结果："+ delete);
                }
                try {
                    boolean create = imageFile.createNewFile();
                    LogUtil.d(TAG,"图片创建结果："+ create);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    /** android 7 到 9 */
                    //7.0 需要配置文件共享：内容提供器，所以需要在manifest注册
                    LogUtil.d(TAG,"packageName : " + activity.getPackageName());
                    imageUri = FileProvider.getUriForFile(activity,activity.getPackageName()+".photo.provider",imageFile);
                }else {
                    /** android 6 及以下 */
                    imageUri = Uri.fromFile(imageFile);
                }
            }
            //启动相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //MediaStore.EXTRA_OUTPUT  意图名称，值：output。表示要使用的内容解析器Uri存储请求的是图像或视频。
            //imageUri ：用来保存拍照的照片
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            activity.startActivityForResult(intent,code);
            LogUtil.d(TAG,"takephoto,imageUri:"+imageUri);
            return imageUri;
        }
        return null;
    }
    /** 保存拍摄的或者选择的照片 */
    @Override
    public void savePhoto(String account,final Bitmap bitmap) {
        //保存在服务器
        getModel().saveHeadPortrait(account, bitmap, new Callback() {
            @Override
            public void success(Object response) {
                Log.d(TAG,"response : " + response.toString());
//                String path = getContext().getExternalFilesDir(DIRECTORY_PICTURES) + File.separator + MallConstant.PHOTO_NAME;
//                LogUtil.d(TAG,"savePhoto path:"+path);
//                File headPhoto = new File(path);
//                if(headPhoto.exists()){
//                    boolean delete = headPhoto.delete();
//                    LogUtil.d(TAG,"savePhoto delete old photo : " + delete);
//                }
//                BufferedOutputStream outputStream = null;
//                try {
//                    outputStream = new BufferedOutputStream(new FileOutputStream(headPhoto));
//                    //第二个参数 压缩比重，图片存储在磁盘上的大小会根据这个值变化。值越小存储在磁盘的图片文件越小（如果是 PNG 格式，第二个参数无效）
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
//                    LogUtil.d(TAG,"savePhoto end");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }finally {
//                    try {
//                        if(outputStream!=null){
//                            outputStream.close();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }

                //本地进行缓存
                ImageUtil.saveHeadCache(bitmap);
                //通知activity 头像更换，保存成功
                getView().OnSavePhotoResult(MallConstant.SUCCESS,"");
            }

            @Override
            public void fail(String info) {
                getView().OnSavePhotoResult(MallConstant.FAIL,info);
            }
        });

    }



    /** 获取照片路径 */
    public String getImagePath(Uri uri, String selection) {
        String path = "";
        //通过内容接收器查询
        Cursor cursor = getContext().getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        LogUtil.d(TAG,"getImagePath,path:"+path);
        return path;
    }
    /** 通知 activity 显示头像 */
    public void displayImage(String imagePath) {
        LogUtil.d(TAG,"imagePath : " + imagePath);
        //   /storage/emulated/0/Pictures/WeiXin/mmexport1600852419852.jpg
        if(!imagePath.isEmpty()){
            //新头像位图 (此步读取文件需要申请读写权限，否则返回 null)
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            LogUtil.d(TAG,"bitmap is null ? " + (bitmap==null));
            //设置固定大小
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,300,300);
            LogUtil.d(TAG,"bitmap is null ? " + (bitmap==null));
            bitmap = ImageUtil.createCircleBitmap(bitmap);
            getView().OnShowImage(bitmap);
        }else {
            LogUtil.d(TAG,"imagePath 为空");
        }
    }
    /** api<19，处理图片方式 */
    private void handleImageOnBefore19(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

}
