package com.cm.fm.mall.view.fragment.menu;


import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseMVPFragment;
import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.model.constant.MallConstant;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.loader.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FoundFragment extends BaseMVPFragment implements View.OnClickListener {
    private Context _context;
    private Banner b_banner;
    private SeekBar sb_seekbar;
    private MyImageLoader imageLoader;
    private VideoView vv_video;
    private ImageButton ib_play,ib_cycle;
    private TextView tv_cur_time,tv_max_time;
    private String tag = "TAG_FoundFragment";
    private boolean isPlay = true;      //是否正在播放
    private boolean isLoaded = false;   //是否加载完成
    private List<Integer> imagePath = new ArrayList<>();
    private List<String> textLists = new ArrayList<>();

    private Handler handler = new Handler();
    //监听播放进度
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (vv_video.isPlaying()){
                int currentPosition = vv_video.getCurrentPosition();
                //根据视频当前进度 设置进度条进度 以及当前时长
                sb_seekbar.setProgress(currentPosition);
                tv_cur_time.setText(timeFormat(currentPosition));
            }
            handler.postDelayed( runnable ,200);
        }
    };
    @Override
    public int initLayout() {
        return R.layout.fragment_found;
    }
    @Override
    protected BasePresenter createPresenter() {
        return null;
    }
    @Override
    public void initView(View view) {
        _context = getActivity();
        initData();
        imageLoader = new MyImageLoader();
        b_banner = view.findViewById(R.id.b_banner);
        vv_video = view.findViewById(R.id.vv_video);

        ib_play = view.findViewById(R.id.ib_play);      //播放、暂停键
        ib_cycle = view.findViewById(R.id.ib_cycle);    //重复播放键
        tv_cur_time = view.findViewById(R.id.tv_cur_time);  //视频播放的当前时间
        tv_max_time = view.findViewById(R.id.tv_max_time);  //视频最大时间
        sb_seekbar = view.findViewById(R.id.sb_seekbar);    //进度条

        ib_play.setOnClickListener(this);
        ib_cycle.setOnClickListener(this);
        //进度条拖动监听
        sb_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度值改变时回调

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始拖动回调方法
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TODO 停止拖动回调方法
                //获取当前进度值
                int progress = seekBar.getProgress();
                if(vv_video.isPlaying()){
                    //设置当前播放位置
                    vv_video.seekTo(progress);
                    tv_cur_time.setText(timeFormat(vv_video.getCurrentPosition()));
                }
            }
        });

        //轮播图
        setBanner();
        //视频
        setVideo();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_play:
                if(isLoaded){
                    if(isPlay){
                        //暂停播放，并切换为播放背景
                        vv_video.pause();
                        ib_play.setBackground(getResources().getDrawable(R.mipmap.bg_play2));
                        isPlay = false;
                    }else {
                        //继续播放，并切换为暂停背景
                        vv_video.start();
                        ib_play.setBackground(getResources().getDrawable(R.mipmap.bg_pause2));
                        isPlay = true;
                    }
                }
                break;
            case R.id.ib_cycle:
                //重新开始播放
                if(isLoaded){
//                    vv_video.resume();
                    vv_video.seekTo(0);
                    vv_video.pause();
                    sb_seekbar.setProgress(0);
                    tv_cur_time.setText("00:00");
                    ib_play.setBackground(getResources().getDrawable(R.mipmap.bg_play2));
                    isPlay = false;
                }
                break;
        }
    }

    @Override
    public void dataDestroy() {
        handler.removeCallbacks(runnable);
        //释放资源
        vv_video.suspend();
    }
    //轮播图设置
    public void setBanner(){
        //设置banner样式（圆点指示器）
        b_banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        b_banner.setImageLoader(imageLoader);
        //设置动画效果
        b_banner.setBannerAnimation(Transformer.ZoomOutSlide);
        //设置图片文字
        b_banner.setBannerTitles(textLists);
        //轮播时间间隔
        b_banner.setDelayTime(3000);
        //是否自动轮播
        b_banner.isAutoPlay(true);
        //指示器位置，居中显示
        b_banner.setIndicatorGravity(BannerConfig.CENTER);
        //图片
        b_banner.setImages(imagePath);
        //轮播监听，替换描述文字
        b_banner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                switch (position){

                }
//                tv_found_tip1.setText(textLists.get(position));
            }
        });
        b_banner.start();
    }
    //视频设置
    public void setVideo(){
        //根据屏幕宽度设置 VideoView 的宽高
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = (int) (width/1.765);   //1.765 视频的宽高比率
        LogUtil.d(tag,"screen size:  "+width+"   "+height);
        ViewGroup.LayoutParams params = vv_video.getLayoutParams();
        params.width = width;
        params.height = height;
        vv_video.setLayoutParams(params);

        //根据路径加载视频,直接播放
        vv_video.setVideoPath(MallConstant.video_url);
        vv_video.setMediaController(new MediaController(_context));
        //默认播放，并设置为暂停背景
        ib_play.setBackground(getResources().getDrawable(R.mipmap.bg_pause2));
        vv_video.requestFocus();

        //缓冲完成时调用
        vv_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isLoaded = true;
                String max_time = timeFormat(vv_video.getDuration());
                LogUtil.d(tag,"max_time:"+max_time);
                tv_max_time.setText(max_time);
                //加载完自动播放
                playVideo();
            }
        });
        vv_video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //视频播放出错时，重新播放
                playVideo();
                return false;
            }
        });
    }
    //播放视频（调用start先加载视频显示画面，然后暂停，让用户主动点击播放）
    public void playVideo(){
        vv_video.start();
        handler.postDelayed(runnable,0);    //立即执行
        //设置进度条的最大值
        sb_seekbar.setMax(vv_video.getDuration());
        //暂停播放，并切换为播放背景
        ib_play.setBackground(getResources().getDrawable(R.mipmap.bg_play2));
        vv_video.pause();
        isPlay = false;
    }

    //时间处理
    public String timeFormat(int time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return simpleDateFormat.format(calendar.getTime());

    }
    //轮播图文本数据
    private void initData(){
        imagePath.add(R.mipmap.p1);
        imagePath.add(R.mipmap.p2);
        imagePath.add(R.mipmap.p3);
        imagePath.add(R.mipmap.p4);
        textLists.add("华为nova7 se 5G手机");
        textLists.add("荣耀30 Pro 50倍远摄 麒麟990 5G");
        textLists.add("荣耀30 50倍远摄 麒麟985 5G");
        textLists.add("荣耀30S 麒麟820 5G芯片");
    }

    //轮播图重写图片加载器
    class MyImageLoader extends ImageLoader{
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext()).load(path).into(imageView);
        }
    }
}
