package app.originality.com.originality.video;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.io.File;
import java.util.List;

import app.originality.com.originality.R;
import app.originality.com.originality.ui.BaseActivity;
import app.originality.com.originality.ui.VideoListActivity;
import app.originality.com.originality.util.AndroidSystemHelper;
import app.originality.com.originality.util.ToastUtils;
import app.originality.com.originality.util.interfaces.InterfaceUtils;
import app.originality.com.originality.util.media.MediaHelper;
import app.originality.com.originality.util.media.MediaPlayerProgressListenner;
import app.originality.com.originality.util.media.MediaType;


/**
 * @author cjy
 * @version V1.0
 * @company 跨越速运
 * @Description 多媒体 视频播放界面
 * @date 2016/9/17
 */
public class VideoPlayerActivity extends BaseActivity implements SurfaceHolder.Callback2, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private RelativeLayout mParentLayout;
    private RelativeLayout mSurfaceLayout;
    private LinearLayout mPalyerLayout;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private MediaType mMediaType;
    private String mUrl;

    private Button mStartPalyer, mLastOne, mNextOne;
    private SeekBar mSeekBarProgress;
    private Button mScreenFullType;         //视频占据屏幕大小模式 0 自适应 1 全屏
    private Button mScreenType;         //视频占据屏幕大小模式 0 竖屏 1 横屏
    private int mCurrentProgress;       //平均播放值
    private int mDuration;              //播放总长度值
    private int mCurrentPosition;       //播放进度值
    private boolean isStart;            //标识 多媒体是否已经开始播放
    private boolean isCan = true;       //是否接受进度条变化操作

    private List<File> mVideFiles;     //视频文件

    @Override
    protected int setView() {
        return R.layout.activity_video_player;
    }


    @Override
    protected void findViews() {
        mParentLayout = (RelativeLayout) findViewById(R.id.parent_layout);
        mPalyerLayout = (LinearLayout) findViewById(R.id.player_item);
        mSurfaceLayout = (RelativeLayout) findViewById(R.id.surface_layout);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mStartPalyer = (Button) findViewById(R.id.start);
        mLastOne = (Button) findViewById(R.id.last_one);
        mNextOne = (Button) findViewById(R.id.next_one);
        mSeekBarProgress = (SeekBar) findViewById(R.id.id_seekbar);
        mScreenFullType = (Button) findViewById(R.id.screen_full_type);
        mScreenType = (Button) findViewById(R.id.screen_type);
        startAlphaAnimation(mParentLayout);
        startScaleAnimation(mSurfaceLayout);
    }


    int count = 0;  //用于标识 播放按钮是否还未点击过

    @Override
    protected void initEvent() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSeekBarProgress.setOnSeekBarChangeListener(this);
        mStartPalyer.setOnClickListener(this);
        mLastOne.setOnClickListener(this);
        mNextOne.setOnClickListener(this);
        mScreenType.setOnClickListener(this);
        mScreenFullType.setOnClickListener(this);
        mPalyerLayout.setOnClickListener(this);
    }


    @Override
    protected void init() {
        hideActionBar();
        mMediaType = (MediaType) getIntent().getSerializableExtra("MediaType");
        mUrl = getIntent().getStringExtra("video_path");
        mIndex = getIntent().getIntExtra("index", 0);
    }

    @Override
    protected void loadData() {
        this.mVideFiles = VideoListActivity.mVideFiles;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                File file = Environment.getExternalStorageDirectory();
//                if (file != null) {
//                    searchFile(file);
                    mHandler.sendEmptyMessage(0);
//                }
//            }
//        }).start();
    }

//    private static File videoFile = null;

    /**
     * 递归算法 查找出包含RMVB的视频文件
     *
     * @param file
     */
//    public void searchFile(File file) {
//        if (file.isDirectory()) {
//            File[] files = file.listFiles();
//            if(files != null){
//                for (File f : files) {
//                    searchFile(f);
//                }
//            }
//        } else if (file.getName().endsWith(".mp4")) {
//            videoFile = file;
//            return;
//        }
//        mVideFiles = FileUtils.listFilesInDirWithFilter(file.getPath(), ".mp4");
//    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mVideFiles != null || mVideFiles.size() > 0) {
                mLastOne.setEnabled(true);
                mNextOne.setEnabled(true);
                mStartPalyer.setEnabled(true);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!isStart && count != 0) {
            MediaHelper.getInstance().onRestart();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MediaHelper.getInstance().onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaHelper.getInstance().onStop();
    }


    /**
     * 展示或隐藏播放控件
     *
     * @param show 0隐藏 1展示
     */
    public void tooglePlayerLayout(int show) {
        if (show == 0) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(500);
            mPalyerLayout.startAnimation(alphaAnimation);
            mPalyerLayout.setVisibility(View.INVISIBLE);
        } else {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(500);
            mPalyerLayout.startAnimation(alphaAnimation);
            mPalyerLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 视频播放 大小 0 自适应    1 全屏
     *
     * @param type
     */
    public void setSurfaceViewOnScreenFullType(int type) {
        int screentWidth = AndroidSystemHelper.getScreenWidth(this);
        int screentHeight = AndroidSystemHelper.getScreenHeight(this);
        int defaultWidth = screentWidth - (screentWidth / 6);
        int defaultHeight = screentWidth - (screentHeight / 3);
        if (type == 0) {
            mSurfaceView.getLayoutParams().width = defaultWidth;
            mSurfaceView.getLayoutParams().height = defaultHeight;
        } else {
            mSurfaceView.getLayoutParams().width = -1;
            mSurfaceView.getLayoutParams().height = -1;
        }
        mSurfaceView.invalidate();
    }

    /**
     * 视频播放 模式 0 竖屏    1 横屏
     *
     * @param type
     */
    public void setSurfaceViewOnScreenType(int type) {
        if (type == 0) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    /**
     * 初始化 文件长度、整除100后的平均值
     *
     * @param duration
     */
    public void initProgress(int duration) {
        mCurrentProgress = duration / 100;
    }


    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //如果进度条改变不是因为音乐播放的回调 而是因为手动改变
        if (!isCan) {
            MediaHelper.getInstance().seekTo(progress);
            new MyMediaPlayerProgressListenner().onPlayerChange(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isCan = false;
        MediaHelper.getInstance().onPause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isCan = true;
        if (mStartPalyer.getText().toString().equals(getResources().getString(R.string.pause_player))) {
            MediaHelper.getInstance().onStart();
        }
    }

    public int mIndex = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //隐藏或者显示 播放ITEM
            case R.id.player_item:
                if (mPalyerLayout.getVisibility() == View.VISIBLE) {
                    tooglePlayerLayout(0);
                } else {
                    tooglePlayerLayout(1);
                }
                break;
            //上一个
            case R.id.last_one:
                if (mVideFiles != null && mVideFiles.size() > 0) {
                    if (mIndex - 1 >= 0) {
                        --mIndex;
                        mMediaType = MediaType.VIDEO_LOCATION;
                        MediaHelper.getInstance().playerVideo(mMediaType, mVideFiles.get(0).getPath(), mSurfaceHolder, new MyMediaPlayerProgressListenner());
                        mStartPalyer.setText(getResources().getString(R.string.pause_player));
                        MediaHelper.getInstance().onStart();
                        isStart = true;
                    }
                } else {
                    ToastUtils.showBigToast(VideoPlayerActivity.this, "当前为第一个视频!");
                }
                break;
            //播放 或暂停
            case R.id.start:
                if (count == 0) {
//                    MediaHelper.getInstance().playerVideo(mMediaType, mUrl, mSurfaceHolder, new MyMediaPlayerProgressListenner());
                    if (mVideFiles != null && mVideFiles.size() > 0) {
                        mMediaType = MediaType.VIDEO_LOCATION;
                        MediaHelper.getInstance().playerVideo(mMediaType, mVideFiles.get(mIndex).getPath(), mSurfaceHolder, new MyMediaPlayerProgressListenner());
                    }
                    mStartPalyer.setText(getResources().getString(R.string.pause_player));
                    isStart = true;
                    count++;
                    return;
                }
                if (mStartPalyer.getText().toString().equals(getResources().getString(R.string.start_player))) {
                    mStartPalyer.setText(getResources().getString(R.string.pause_player));
                    MediaHelper.getInstance().onStart();
                    isStart = true;
                } else {
                    mStartPalyer.setText(getResources().getString(R.string.start_player));
                    isStart = false;
                    MediaHelper.getInstance().onPause();
                }
                break;
            //下一个
            case R.id.next_one:
                if (mVideFiles != null && mVideFiles.size() > 0) {
                    if (mIndex + 1 <= mVideFiles.size() - 1) {
                        ++mIndex;
                        mMediaType = MediaType.VIDEO_LOCATION;
                        MediaHelper.getInstance().playerVideo(mMediaType, mVideFiles.get(mIndex).getPath(), mSurfaceHolder, new MyMediaPlayerProgressListenner());
                        mStartPalyer.setText(getResources().getString(R.string.pause_player));
                        MediaHelper.getInstance().onStart();
                        isStart = true;
                    }
                } else {
                    ToastUtils.showBigToast(VideoPlayerActivity.this, "当前为最后一个视频!");
                }
                break;

            //横 竖屏切换
            case R.id.screen_type:
                if (mScreenType.getText().toString().equals(getResources().getString(R.string.portrait_screen))) {
                    mScreenType.setText(getResources().getString(R.string.landscape_screen));
                    setSurfaceViewOnScreenType(0);
                } else {
                    mScreenType.setText(getResources().getString(R.string.portrait_screen));
                    setSurfaceViewOnScreenType(1);
                }
                break;

            //全屏和自适应切换
            case R.id.screen_full_type:
                if (mScreenFullType.getText().toString().equals(getResources().getString(R.string.inside_screen))) {
                    mScreenFullType.setText(getResources().getString(R.string.full_screen));
                    setSurfaceViewOnScreenFullType(0);
                } else {
                    mScreenFullType.setText(getResources().getString(R.string.inside_screen));
                    setSurfaceViewOnScreenFullType(1);
                }
                break;

        }
    }

    public class MyMediaPlayerProgressListenner implements MediaPlayerProgressListenner {

        @Override
        public void onPlayerStart(int duration) {
            isStart = true;
            isCan = true;
            mDuration = duration;
            initProgress(duration);
            mSeekBarProgress.setMax(duration);
        }

        @Override
        public void onPlayerChange(int currentPosition) {
            if (isCan) {
                mCurrentPosition = currentPosition;
                mSeekBarProgress.setProgress(currentPosition);
            }
        }

        @Override
        public void onPlayerPause() {

        }

        @Override
        public void onPlayerStop() {
            isStart = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void goBack() {
        endScaleAnimation(mSurfaceLayout);
        endAlphaAnimation(mParentLayout);
    }

    public void startAlphaAnimation(View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(800);
        view.startAnimation(alphaAnimation);
    }

    public void endAlphaAnimation(View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(800);
        view.startAnimation(alphaAnimation);
    }

    public void startScaleAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        view.startAnimation(scaleAnimation);
    }

    public void endScaleAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.1f, 1.0f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        view.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new InterfaceUtils.MyAnimationListenner() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                mParentLayout.setVisibility(View.INVISIBLE);
                mSurfaceLayout.setVisibility(View.INVISIBLE);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

}
