package com.cm.fm.mall.view.activity;

//import android.Manifest;
//import android.annotation.TargetApi;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.transition.Transition;
//import android.transition.TransitionInflater;
//import android.util.Log;
//import android.view.Window;
//
//import com.baidu.location.BDAbstractLocationListener;
//import com.baidu.location.BDLocation;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.SDKInitializer;
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.MapStatusUpdate;
//import com.baidu.mapapi.map.MapStatusUpdateFactory;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.MyLocationData;
//import com.baidu.mapapi.model.LatLng;
import com.cm.fm.mall.MyApplication;
import com.cm.fm.mall.R;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.Utils;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class LocationActivity extends AppCompatActivity {
//    private String tag = "TAG_LocationActivity";
//    private final int SDK_PERMISSION_REQUEST = 800;
//    private String permissionInfo = "";
//    private LocationClient client;
//    private MapView mv_mapview;
//    private BaiduMap baiduMap;
//    private ArrayList<String> permissions;
//    private boolean isFirstLocate = true;
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //告知页面，使用动画
//Utils.actUseAnim(context);
//
//        //百度定位sdk初始化，setContentView 之前调用
//        client = new LocationClient(MyApplication.getContext());
//        client.registerLocationListener(new MyLocationListener());
//        LocationClientOption option = new LocationClientOption();
//        //设置定位模式（此处高精度模式：表示优先使用GPS，无法GPS时使用网络定位）
//        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
//        option.setOpenGps(true);
//        //获取当前位置的详细地址信息
//        option.setIsNeedAddress(true);
//        //获取位置描述信息
//        option.setIsNeedLocationDescribe(true);
//        option.setScanSpan(5000);
//        //设置坐标系类型，三种
//        option.setCoorType("bd09ll");
//        client.setLocOption(option);
//        //TODO 加载视图
//        setContentView(R.layout.activity_location);
//
//        mv_mapview = findViewById(R.id.mv_mapview);
//        //地图总控制器
//        baiduMap = mv_mapview.getMap();
//        // 开启定位图层
//        baiduMap.setMyLocationEnabled(true);
//
//        //定位权限检测
//        checkPermission();
//
//    }
//
//    private void checkPermission(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            permissions = new ArrayList<String>();
//            /***
//             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
//             */
//            // 定位精确位置
//            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                LogUtil.d(tag,"定位权限获取失败：Manifest.permission.ACCESS_FINE_LOCATION");
//                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
//            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                LogUtil.d(tag,"定位权限获取失败：Manifest.permission.ACCESS_COARSE_LOCATION");
//                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//            }
//
//            /**
//             * 读写权限和电话状态权限非必要权限只会申请一次，用户同意或者禁止，只会弹一次
//             */
//            // 读写权限
//            if (addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
//                LogUtil.d(tag,permissionInfo);
//            }
//            LogUtil.d(tag,"permission list : " + permissions.size());
//            //有需要申请的权限，进行申请
//            if (permissions.size() > 0) {
//                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
//            }else {
//                //否则直接进行定位
//                requestLocation();
//            }
//
//        }
//    }
//
//    //读写权限处理。用户勾选了下次不在询问，则只申请本次
//    @TargetApi(23)
//    private boolean addPermission(String permission) {
//        // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
//        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//            //用户是否勾选了下次不在询问
//            if (shouldShowRequestPermissionRationale(permission)) {
//                return true;
//            } else {
//                permissions.add(permission);
//                return false;
//            }
//        } else {
//            return true;
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode){
//            case SDK_PERMISSION_REQUEST:
//                LogUtil.i(tag,"权限");
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if(grantResults.length > 0 && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//                        Utils.tips(MyApplication.getContext(),"定位权限未同意，无法使用定位功能！");
//                        LocationActivity.this.finish();
//                        return;
//                    }
//                }
//                LogUtil.i(tag,"开始定位");
//                requestLocation();
//                break;
//        }
//    }
//    //请求定位
//    public void requestLocation(){
//        //因为 start 只会执行一次，所以需要每隔指定时间更新一下当前位置
//        client.start();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mv_mapview.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mv_mapview.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // 退出时销毁定位
//        client.stop();
//        // 关闭定位图层
//        baiduMap.setMyLocationEnabled(false);
//        mv_mapview.onDestroy();
//        mv_mapview = null;
//
//    }
//
//
//    public class  MyLocationListener extends BDAbstractLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation location) {
////            LogUtil.d(tag,"location is null? " + (location==null));
//            //此处的 BDLocation 为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
//            if(location != null){
//                StringBuilder sb = new StringBuilder();
//                sb.append("\n")
//                    .append("纬度：").append(location.getLatitude()).append("\n")
//                    .append("经线：").append(location.getLongitude()).append("\n")
//                    .append("国家：").append(location.getCountry()).append("\n")
//                    .append("省：").append(location.getProvince()).append("\n")
//                    .append("市：").append(location.getCity()).append("\n")
//                    .append("区：").append(location.getDirection()).append("\n")
//                    .append("街道：").append(location.getStreet()).append("\n")
//                    .append("定位方式：");
//                if(location.getLocType() == BDLocation.TypeGpsLocation){
//                    sb.append("GPS");
//                }else if(location.getLocType() == BDLocation.TypeNetWorkLocation){
//                    sb.append("移动网络");
//                }
//                LogUtil.d(tag,sb.toString());
//
//                //输出当前经纬度地图 以及 输出我的位置信息
//                if(isFirstLocate){
//                    //TODO 首次定位需要设置地图缩放级别
//                    //经纬度设置。参数一：纬度   参数二：经度
//                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//                    MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
//                    //将地图移动至此经纬度上
//                    baiduMap.animateMapStatus(update);
//                    //地图缩放级别：3-19，小数点也可以，值越大，地图信息越精细
//                    update = MapStatusUpdateFactory.zoomTo(18f);
//                    baiduMap.animateMapStatus(update);
//                    isFirstLocate = false;
//                }
//
//                //将我的定位显示在地图上
//                MyLocationData.Builder builder = new MyLocationData.Builder();
//                builder.latitude(location.getLatitude());
//                builder.longitude(location.getLongitude());
//                MyLocationData data = builder.build();
//                baiduMap.setMyLocationData(data);
//                //获取位置描述信息
//                String locationDescribe = location.getLocationDescribe();
//                LogUtil.d(tag,"locationDescribe: " + locationDescribe);
//            }
//
//
//
//        }
//    }
}
