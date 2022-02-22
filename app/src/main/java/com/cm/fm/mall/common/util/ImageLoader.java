package com.cm.fm.mall.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.cm.fm.mall.R;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

/**
 * 使用 LruCache 和 DiskLruCache 封装一个自己的图片加载器
 * 内存缓存 和 磁盘缓存 是ImageLoader的核心，也是ImageLoader的意义所在，通过这两级缓存极大地提高了程序效率，同时有效降低了用户的流量消耗
 * 一个优秀的ImageLoader应该具备的功能：
 *     1、图片同步加载；
 *     2、图片异步加载；
 *     3、图片压缩；
 *     4、内存缓存；
 *     5、磁盘缓存；
 *     6、网络加载
 *
 * LruCache android3.1提供的一个缓存类（泛型类），内部采用一个LinkedHashMap，以强引用的方式存储外界的缓存对象，并提供了get，put方法完成对缓存的获取和添加。
 * Lru(Least Recently Used) 的意思就是近期最少使用算法，它的核心思想就是会优先淘汰那些近期最少使用的缓存对象
 *  强引用：直接的对象引用
 *  软引用：当对象只有一个软引用存在时，系统内存不足时则会通过gc回收此对象
 *  弱引用：当一个对象只有弱引用存在时，gc会随时可能回收此对象
 *
 * DiskLruCache
 * 不是安卓sdk的api，但是是官方推荐的磁盘缓存三方库。
 * 创建 DiskLruCache：open(File file,int appVersion,int valueCount,long maxSize)
 *      file：磁盘缓存文件在文件系统中的存储路径。如果应用卸载后希望删除文件，则选择SD卡上的缓存目录，如果希望保留数据，则选择sd卡的其他目录
 *      appVersion：应用版本号，一般设置为1。版本号改变，DiskLruCache会清空之前的缓存文件，但那时我们一般是需要版本号就算改变没数据仍因有效，所以用处不大，默认设置为1
 *      valueCount：单个节点所对应的数据个数，一般设置为1。
 *      maxSize：缓存总大小，比如50M。当缓存文件超过大小，则会清除一些缓存，
 *
 */

public class ImageLoader {
    private final String TAG = "ADP_MyImageLoader";

    private final Context mContext;
    private LruCache<String, Bitmap> mMemoryLruCache;
    private DiskLruCache mDiskLruCache;

    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;   //50M
    private static final int DISK_CACHE_INDEX = 0;
    private static final int IO_BUFFER_SIZE = 1024 * 8;             //缓冲区大小，8KB
    private static boolean mIsDiskLruCacheCreated = false;


    public static final int MESSAGE_POST_RESULT = 1;
    //cpu个数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;
    //ImageView的tag对应的key
    private static final int TAG_KEY_URI = R.id.imageLoader_uri;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
        }
    };
    //创建线程池
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), sThreadFactory);

    private final Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;
            ImageView imageView = result.imageView;
            String uri = (String) imageView.getTag(TAG_KEY_URI);
            //防止listview等列表的item快速滑动，导致bitmap加载错位。如果缓存在 imageView 的uri 和当前item的uri相同，则设置bitmap
            if (uri.equals(result.uri)) {
                imageView.setImageBitmap(result.bitmap);
            } else {
                Log.w(TAG, "set image bitmap,but url has changed, ignored!");
            }
        };
    };


    public ImageLoader(Context context) {
        //建议使用应用上下文，如果是activity上下文用在 ImageLoader 中，如果ImageLoader采用单例模式，容易导致内存泄露
        this.mContext = context.getApplicationContext();
        /* 初始化LruCache 和 DiskLruCache  */
        initCache();
    }

    private void initCache(){
        //最大内存。除以1024是将单位转为kb
        int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
        //缓存内存只允许使用内存的8分之一
        int cacheSize = maxMemory / 8;
        mMemoryLruCache = new LruCache<String,Bitmap>(cacheSize){
            @Override   //sizeOf 计算缓存的大小
            protected int sizeOf(String key, Bitmap bitmap) {
                //getByteCount(): 存储该位图像素的最小字节数。api12之后才有，内部封装的getRowBytes() * getHeight()
//                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                return bitmap.getByteCount() / 1024;
            }
        };

        File diskCacheDir = getDiskCacheDir(mContext, "bitmap");
        Log.d(TAG, "getDiskCacheDir: image path : " + diskCacheDir.getPath());
        if(!diskCacheDir.exists()){
            diskCacheDir.mkdirs();  //mkdirs 和 mkdir的区别：mkdirs可以创建多层目录，但是mkdir只能创建一层
        }
        //如果磁盘空间小于磁盘缓存所需空间大小时，不缓存（一般是手机内存不足了）
        if(getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE){
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建一个新实例
     * @param context
     * @return
     */
    public static ImageLoader build(Context context){
        return new ImageLoader(context);
    }

    /**
     * 异步加载Bitmap
     * 必须在主线程调用
     */
    public void bindBitmap(String uri, ImageView imageView) {
        bindBitmap(uri, imageView, 0, 0);
    }
    public void bindBitmap(final String uri, final ImageView imageView, final int reqWidth, final int reqHeight) {
        //给当前imageView的设置一个tag（当异步完成后需要比对tag，防止列表滑动item导致bitmap加载错位）
        imageView.setTag(TAG_KEY_URI, uri);
        //内存缓存读取bitmap
        Bitmap bitmap = loadBitmapFromMemoryCache(uri);
        if (bitmap != null) {
            Log.d(TAG, "从内存缓存中加载的图片，uri = "+uri);
            imageView.setImageBitmap(bitmap);
            return;
        }
        //异步加载
        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(uri, reqWidth, reqHeight);
                if (bitmap != null) {
                    LoaderResult result = new LoaderResult(imageView, uri, bitmap);
                    /**
                     * obtainMessage() 与 sendMessage()区别
                     * obtainMessage()有多个重载方法，可指定what标记，一般搭配 sendToTarget()使用。会利用内部的message池，如果池中有message，则不重新new分配，
                     *      直接复用，达到节省开支的效果。源码可看到，sendToTarget()内部是调用的sendMessage()，
                     * 两个都可达到相同目的。但是推荐使用obtainMessage()+sendToTarget()
                     * */
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT, result).sendToTarget();
//                    mMainHandler.sendMessage(result);
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    /**
     * 同步加载bitmap
     * 同步加载需要外部在线程中调用，这是因为同步加载很可能比较耗时
     *
     * 同步加载步骤：内存缓存读取，磁盘缓存读取，网络加载
     */
    public Bitmap loadBitmap(String url,int reqWidth,int reqHeight){
        //内存缓存获取
        Bitmap bitmap = loadBitmapFromMemoryCache(url);
        if (bitmap != null) {
            Log.d(TAG, "从内存缓存中加载的图片，url:" + url);
            return bitmap;
        }
        try {
            //磁盘缓存获取
            bitmap = loadBitmapFromDiskCache(url, reqWidth, reqHeight);
            if (bitmap != null) {
                Log.d(TAG, "从磁盘缓存中加载的图片，url:" + url);
                return bitmap;
            }
            //网络获取，并将bitmap添加到磁盘缓存
            bitmap = loadBitmapFromHttp(url, reqWidth, reqHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null && !mIsDiskLruCacheCreated) {
            Log.w(TAG, "encounter error, DiskLruCache is not created.");
            //再次从网络获取，不缓存
            bitmap = downloadBitmapFromUrl(url);
            Log.d(TAG, "从网络下载的图片，url:" + url);
        }
        return bitmap;
    }


    /* 通过url加载Bitmap，并将bitmap添加到磁盘缓存 */
    private Bitmap loadBitmapFromHttp(String url,int reqWidth,int reqHeight) throws IOException{
        if(Looper.myLooper() == Looper.getMainLooper()){
            throw new RuntimeException("can not visit network from UI Thread.");
        }
        if(mDiskLruCache == null){
            return null;
        }
        String key = hashKeyFormUrl(url);
        //磁盘缓存需要通过Editor完成
        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
        if(editor != null){
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            if(downloadUrlToStream(url,outputStream)){
                //转换成功，则调用commit()写到磁盘中
                editor.commit();
            }else {
                //转换失败，撤销写操作，调用abort()中止editor，并释放editor锁
                editor.abort();
            }
            mDiskLruCache.flush();
        }
        return loadBitmapFromDiskCache(url,reqWidth,reqHeight);
    }


    /* 从磁盘缓存中加载bitmap，不可在主线程中调用*/
    private Bitmap loadBitmapFromDiskCache(String url, int reqWidth, int reqHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "load bitmap from UI Thread, it's not recommended!");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        Bitmap bitmap = null;
        String key = hashKeyFormUrl(url);
        //获取 Snapshot 对象（snapshot 快照），通过当前缓存文件的快照获取输入流
        DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
        if (snapShot != null) {
            FileInputStream fileInputStream = (FileInputStream)snapShot.getInputStream(DISK_CACHE_INDEX);
            //获取文件描述符
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            //通过文件描述符压缩bitmap
            bitmap = ImageSize.decodeSampledBitmapFromFileDescriptor(fileDescriptor, reqWidth, reqHeight);
            if (bitmap != null) {
                //添加到内存缓存
                addBitmapToMemoryCache(key, bitmap);
            }
        }
        return bitmap;
    }


    /*  将url图片转为流 */
    public boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection httpURLConnection = null;
        HttpsURLConnection httpsURLConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            URL url = new URL(urlString);
            if (url.getProtocol().toLowerCase().equals("https")) {
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                //创建一个指定缓冲区大小的 BufferedInputStream、BufferedOutputStream。本身默认也是8kb
                in = new BufferedInputStream(httpsURLConnection.getInputStream(), IO_BUFFER_SIZE);
            }else {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                //创建一个指定缓冲区大小的 BufferedInputStream、BufferedOutputStream。本身默认也是8kb
                in = new BufferedInputStream(httpURLConnection.getInputStream(), IO_BUFFER_SIZE);
            }
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "downloadBitmap failed." + e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
            Utils.close(out);
            Utils.close(in);
        }
        return false;
    }



    /* 通过url从网络加载bitmap */
    private Bitmap downloadBitmapFromUrl(String urlString) {
        Bitmap bitmap = null;
        HttpURLConnection httpURLConnection = null;
        HttpsURLConnection httpsURLConnection = null;
        BufferedInputStream in = null;

        try {
            URL url = new URL(urlString);
            if (url.getProtocol().toLowerCase().equals("https")) {
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                in = new BufferedInputStream(httpsURLConnection.getInputStream(), IO_BUFFER_SIZE);
            }else {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(httpURLConnection.getInputStream(), IO_BUFFER_SIZE);
            }
            bitmap = BitmapFactory.decodeStream(in);
        } catch (final IOException e) {
            Log.e(TAG, "Error in downloadBitmap: " + e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
            Utils.close(in);
        }
        return bitmap;
    }


    /*
     * 将图片url转为md5 key
     * 图片url可能包含很多特殊字符，会影响url在android中使用，一般采用url的md5值作为key
     * */
    private String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            cacheKey = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            //转为16进制
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }




    /* 磁盘缓存获取存储路径
    * @param uniqueName 唯一的名字
    * */
    public File getDiskCacheDir(Context context, String uniqueName) {
        //判断存储卡是否可用
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + uniqueName);
    }

    @SuppressLint("UsableSpace")
    private long getUsableSpace(File path) {
        return path.getUsableSpace();
    }

    /* 向内存缓存中添加bitmap */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if(getBitmapFromMemoryCache(key)==null){
            mMemoryLruCache.put(key,bitmap);
        }
    }

    /* 内存缓存中获取bitmap，结果可能为null */
    private Bitmap getBitmapFromMemoryCache(String key){
        return mMemoryLruCache.get(key);
    }
    private Bitmap loadBitmapFromMemoryCache(String url) {
        final String key = hashKeyFormUrl(url);
        return getBitmapFromMemoryCache(key);
    }
    /* 内存缓存中删除bitmap */
    private void removeBitmapFromMemoryCache(String key){
        if(mMemoryLruCache.get(key)!=null){
            mMemoryLruCache.remove(key);
        }
    }


    /** 将需要加载的图片、图片地址，以及需要绑定的ImageView封装成 LoaderResult 类 */
    private static class LoaderResult {
        public ImageView imageView;
        public String uri;
        public Bitmap bitmap;

        public LoaderResult(ImageView imageView, String uri, Bitmap bitmap) {
            this.imageView = imageView;
            this.uri = uri;
            this.bitmap = bitmap;
        }
    }
}
